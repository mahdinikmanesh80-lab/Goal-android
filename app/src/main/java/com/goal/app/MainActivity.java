
package com.goal.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import android.content.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    LinearLayout root, content;
    JSONArray activities = new JSONArray();
    SharedPreferences prefs;
    int blue = Color.rgb(55, 105, 220);

    int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        prefs = getSharedPreferences("goal_data", MODE_PRIVATE);
        try {
            activities = new JSONArray(prefs.getString("activities", "[]"));
        } catch(Exception e) {}

        showHome();
    }

    TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.rgb(35, 45, 65));
        t.setPadding(dp(8), dp(10), dp(8), dp(10));
        return t;
    }

    Button button(String value, Runnable action) {
        Button b = new Button(this);
        b.setText(value);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    void setup(String title) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(246, 248, 253));

        TextView header = text(title, 25);
        header.setTextColor(blue);
        header.setPadding(dp(20), dp(22), dp(20), dp(18));
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setPadding(dp(16), dp(8), dp(16), dp(20));
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));

        setContentView(root);
    }

    void showHome() {
        setup("Goal  ✦");

        content.addView(text("به برنامه Goal خوش آمدی!", 20));
        content.addView(text(
            "قدم‌های کوچک امروز، موفقیت‌های بزرگ فردا.",
            15
        ));

        content.addView(button("🎯  اهداف من", () -> showGoals()));
        content.addView(button("📅  برنامه روزانه", () -> showTasks()));
        content.addView(button("⭐  فعالیت‌های من", () -> showActivities()));
        content.addView(button("💰  امور مالی", () -> showFinance()));
        content.addView(button("📊  گزارش‌ها", () -> showReports()));
        content.addView(button("⚙️  تنظیمات", () -> showSettings()));
    }

    void showActivities() {
        setup("فعالیت‌های من");

        content.addView(text(
            "فعالیت‌های دلخواهت را اضافه کن. هیچ فعالیت پیش‌فرضی وجود ندارد.",
            15
        ));

        content.addView(button("+ افزودن فعالیت جدید", () -> addActivity()));

        for (int i = 0; i < activities.length(); i++) {
            final int index = i;
            JSONObject item = activities.optJSONObject(i);
            if (item == null) continue;

            String name = item.optString("name");
            String unit = item.optString("unit");
            int target = item.optInt("target", 1);
            int value = item.optInt("value", 0);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(Color.WHITE);

            card.addView(text(name, 18));
            card.addView(text(
                value + " / " + target + " " + unit, 14
            ));

            LinearLayout actions = new LinearLayout(this);

            Button minus = new Button(this);
            minus.setText("−");
            minus.setOnClickListener(v -> changeValue(index, -1));

            Button plus = new Button(this);
            plus.setText("+");
            plus.setOnClickListener(v -> changeValue(index, 1));

            Button delete = new Button(this);
            delete.setText("حذف");
            delete.setOnClickListener(v -> removeActivity(index));

            actions.addView(minus);
            actions.addView(plus);
            actions.addView(delete);
            card.addView(actions);

            LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
            p.setMargins(0, dp(6), 0, dp(6));
            content.addView(card, p);
        }

        content.addView(button("بازگشت به خانه", () -> showHome()));
    }

    void addActivity() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(this);
        name.setHint("نام فعالیت");

        EditText unit = new EditText(this);
        unit.setHint("واحد (مثلاً دقیقه یا لیوان)");

        EditText target = new EditText(this);
        target.setHint("هدف روزانه");
        target.setInputType(2);

        form.addView(name);
        form.addView(unit);
        form.addView(target);

        new AlertDialog.Builder(this)
            .setTitle("فعالیت جدید")
            .setView(form)
            .setPositiveButton("ذخیره", (d, w) -> {
                try {
                    String n = name.getText().toString().trim();
                    String u = unit.getText().toString().trim();
                    int t = Integer.parseInt(
                        target.getText().toString()
                    );

                    if (n.isEmpty() || u.isEmpty() || t < 1)
                        return;

                    JSONObject item = new JSONObject();
                    item.put("name", n);
                    item.put("unit", u);
                    item.put("target", t);
                    item.put("value", 0);

                    activities.put(item);
                    save();
                    showActivities();
                } catch(Exception e) {
                    Toast.makeText(this,
                        "اطلاعات را بررسی کن",
                        Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("انصراف", null)
            .show();
    }

    void changeValue(int index, int amount) {
        JSONObject item = activities.optJSONObject(index);
        if (item == null) return;

        int value = Math.max(
            0, item.optInt("value", 0) + amount
        );

        try {
            item.put("value", value);
            save();
        } catch(Exception e) {}

        showActivities();
    }

    void removeActivity(int index) {
        JSONArray updated = new JSONArray();

        for (int i = 0; i < activities.length(); i++) {
            if (i != index)
                updated.put(activities.optJSONObject(i));
        }

        activities = updated;
        save();
        showActivities();
    }

    void save() {
        prefs.edit().putString(
            "activities", activities.toString()
        ).apply();
    }

    void showGoals() {
        simplePage("اهداف من",
            "اهداف کوتاه‌مدت و بلندمدتت را دنبال کن.");
    }

    void showTasks() {
        simplePage("برنامه روزانه",
            "برنامه روزانه در نسخه‌های بعدی تکمیل می‌شود.");
    }

    void showFinance() {
        simplePage("امور مالی",
            "ثبت دستی درآمد و هزینه در نسخه‌های بعدی تکمیل می‌شود.");
    }

    void showReports() {
        simplePage("گزارش‌ها",
            "گزارش پیشرفت در نسخه‌های بعدی تکمیل می‌شود.");
    }

    void showSettings() {
        setup("تنظیمات");

        content.addView(text("برنامه Goal", 20));
        content.addView(text(
            "اطلاعات برنامه به‌صورت محلی روی گوشی ذخیره می‌شود.",
            15
        ));
        content.addView(button("فارسی", () -> showHome()));
        content.addView(button("English", () -> {
            Toast.makeText(this,
                "English will be added in the next version",
                Toast.LENGTH_SHORT).show();
        }));
        content.addView(button("بازگشت", () -> showHome()));
    }

    void simplePage(String title, String message) {
        setup(title);
        content.addView(text(message, 17));
        content.addView(button("بازگشت به خانه", () -> showHome()));
    }
}
