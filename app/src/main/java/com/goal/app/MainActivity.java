package com.goal.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, content;
    SharedPreferences prefs;
    String lang="fa", today, dailyMotivation;
    boolean dark=false;
    JSONArray goals, tasks, activities, transactions, debts, assets;
    int blue=Color.rgb(91,105,255);
    int purple=Color.rgb(142,76,255);
    int cyan=Color.rgb(54,190,255);
    int surface=Color.rgb(27,35,67);

    int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density); }
    String tr(String fa,String en){ return lang.equals("fa")?fa:en; }
    String date(){ return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date()); }
    String persianDate(){ return DateFormat.getDateInstance(DateFormat.FULL,Locale.forLanguageTag("fa-IR-u-ca-persian")).format(new Date()); }
    int bg(){ return dark?Color.rgb(10,15,35):Color.rgb(239,243,255); }
    int fg(){ return dark?Color.rgb(244,246,255):Color.rgb(30,39,72); }
    GradientDrawable shape(int a,int b,int stroke){
        GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{a,b});
        d.setCornerRadius(dp(22));
        if(stroke>0)d.setStroke(dp(1),stroke);
        return d;
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("goal_data",MODE_PRIVATE);
        lang=prefs.getString("language","fa");
        dark=prefs.getBoolean("dark",false);
        today=date();
        goals=load("goals"); tasks=load("tasks"); activities=load("activities");
        transactions=load("transactions"); debts=load("debts"); assets=load("assets");
        normalizeDaily();
        dailyMotivation=pickMotivation();
        home();
    }
    String pickMotivation(){
        String[] fa={
            "هر قدم کوچک، تو را به رؤیای بزرگت نزدیک‌تر می‌کند.",
            "امروز فرصت تازه‌ای است برای ساختن بهترین نسخه خودت.",
            "لازم نیست کامل باشی؛ فقط کافی است ادامه بدهی.",
            "موفقیت از تصمیم‌های کوچک و تکرارهای روزانه ساخته می‌شود.",
            "به خودت ایمان داشته باش؛ تو از چیزی که فکر می‌کنی توانمندتری.",
            "آینده‌ات را با انتخاب‌های امروزت می‌سازی.",
            "آهسته رفتن اشکالی ندارد؛ فقط متوقف نشو.",
            "به جای منتظر ماندن برای انگیزه، با یک قدم شروع کن.",
            "هر روز یک فرصت برای بهتر شدن است.",
            "تو برای پیشرفت ساخته شده‌ای؛ امروز را از دست نده."
        };
        String[] en={
            "Every small step brings you closer to a big dream.",
            "Today is a fresh chance to become your best self.",
            "You don't have to be perfect; just keep going.",
            "Success is built from small decisions repeated daily.",
            "Believe in yourself; you are stronger than you think.",
            "Your future is shaped by the choices you make today.",
            "It's okay to go slowly. Just don't stop.",
            "Don't wait for motivation. Start with one small step.",
            "Every day is another chance to grow.",
            "You were made to grow. Make today count."
        };
        Random random=new Random();
        int index=random.nextInt(fa.length);
        return lang.equals("fa")?fa[index]:en[index];
    }
    JSONArray load(String key){ try{return new JSONArray(prefs.getString(key,"[]"));}catch(Exception e){return new JSONArray();} }
    void save(String key,JSONArray value){ prefs.edit().putString(key,value.toString()).apply(); }
    void saveAll(){ save("goals",goals);save("tasks",tasks);save("activities",activities);save("transactions",transactions);save("debts",debts);save("assets",assets); }
    void normalizeDaily(){
        String last=prefs.getString("last_day","");
        if(!last.equals(today)){
            for(int i=0;i<activities.length();i++){ JSONObject o=activities.optJSONObject(i);if(o!=null)try{o.put("value",0);}catch(Exception e){} }
            prefs.edit().putString("last_day",today).apply();save("activities",activities);
        }
        JSONArray keep=new JSONArray();
        for(int i=0;i<tasks.length();i++){JSONObject o=tasks.optJSONObject(i);if(o!=null && today.equals(o.optString("date")))keep.put(o);}
        tasks=keep;save("tasks",tasks);
    }
    TextView text(String s,int size){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg());
        t.setPadding(dp(10),dp(10),dp(10),dp(10));
        t.setGravity(lang.equals("fa")?Gravity.RIGHT:Gravity.LEFT);
        t.setLineSpacing(dp(2),1.05f);
        return t;
    }
    Button button(String s,Runnable r){
        Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Color.WHITE);
        b.setTextSize(15);b.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);
        b.setPadding(dp(16),dp(12),dp(16),dp(12));
        b.setBackground(shape(purple,blue,Color.argb(110,190,190,255)));
        b.setElevation(dp(5));b.setMinHeight(dp(54));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(6),0,dp(6));b.setLayoutParams(p);
        b.setOnClickListener(v->r.run());return b;
    }
    EditText edit(String hint,int type){
        EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);if(type!=0)e.setInputType(type);
        e.setTextColor(fg());e.setHintTextColor(dark?Color.rgb(165,174,205):Color.rgb(120,130,160));
        e.setPadding(dp(14),dp(10),dp(14),dp(10));e.setBackground(shape(dark?Color.rgb(31,40,75):Color.WHITE,dark?Color.rgb(25,33,65):Color.rgb(230,237,255),Color.rgb(105,116,220)));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(6),0,dp(6));e.setLayoutParams(p);return e;
    }
    void setup(String title){
        getWindow().setStatusBarColor(dark?Color.rgb(10,15,35):Color.rgb(40,49,115));
        getWindow().setNavigationBarColor(dark?Color.rgb(10,15,35):Color.rgb(40,49,115));
        root=new LinearLayout(this);root.setOrientation(1);
        root.setBackground(shape(bg(),dark?Color.rgb(23,29,66):Color.rgb(220,230,255),0));
        LinearLayout hero=new LinearLayout(this);hero.setOrientation(1);hero.setPadding(dp(18),dp(14),dp(18),dp(16));
        hero.setBackground(shape(dark?Color.rgb(53,43,112):Color.rgb(93,105,210),dark?Color.rgb(25,54,111):Color.rgb(126,93,220),Color.argb(120,210,215,255)));
        TextView brand=text("✦  GOAL",14);brand.setTextColor(Color.rgb(205,210,255));brand.setTypeface(null,android.graphics.Typeface.BOLD);
        TextView h=text(title,24);h.setTextColor(Color.WHITE);h.setTypeface(null,android.graphics.Typeface.BOLD);
        TextView sub=text(tr("برنامه‌ریزی کن • تمرکز کن • پیشرفت کن","Plan • Focus • Achieve"),12);sub.setTextColor(Color.rgb(218,224,255));
        hero.addView(brand);hero.addView(h);hero.addView(sub);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.setMargins(dp(14),dp(12),dp(14),dp(8));root.addView(hero,hp);
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);
        content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(14),dp(6),dp(14),dp(24));sc.addView(content);
        root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
    }
    void heading(String s){content.addView(text(s,18));}
    void back(){content.addView(button(tr("⌂ خانه","⌂ Home"),()->home()));}
    void home(){
        setup("Goal ✦");
        LinearLayout quote=card();
        quote.setBackground(shape(dark?Color.rgb(69,48,139):Color.rgb(107,91,220),dark?Color.rgb(31,76,145):Color.rgb(67,143,222),Color.argb(130,220,225,255)));
        TextView quoteLabel=text(tr("✨ جمله انگیزشی امروز","✨ YOUR DAILY INSPIRATION"),12);
        quoteLabel.setTextColor(Color.rgb(225,229,255));
        quoteLabel.setTypeface(null,android.graphics.Typeface.BOLD);
        TextView quoteText=text("“"+dailyMotivation+"”",19);
        quoteText.setTextColor(Color.WHITE);
        quoteText.setTypeface(null,android.graphics.Typeface.BOLD);
        quote.addView(quoteLabel);quote.addView(quoteText);
        LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(-1,-2);qp.setMargins(0,dp(4),0,dp(12));quote.setLayoutParams(qp);
        content.addView(quote);
        content.addView(text(tr("به Goal خوش آمدی!","Welcome to Goal!"),21));
        content.addView(text(tr("قدم‌های کوچک امروز، موفقیت‌های بزرگ فردا.","Small steps today, big achievements tomorrow."),15));
        content.addView(text("📆  "+persianDate(),15));
        content.addView(button("🗓️  "+tr("تقویم شمسی","Persian calendar"),()->calendarPage()));
        content.addView(button("🎯  "+tr("اهداف من","My Goals"),()->goalPage()));
        content.addView(button("📅  "+tr("برنامه امروز","Today's Plan"),()->taskPage()));
        content.addView(button("⭐  "+tr("فعالیت‌های من","My Activities"),()->activityPage()));
        content.addView(button("💰  "+tr("امور مالی","Finance"),()->financePage()));
        content.addView(button("📊  "+tr("گزارش‌ها","Reports"),()->reports()));
        content.addView(button("⚙️  "+tr("تنظیمات","Settings"),()->settings()));
        int done=0;for(int i=0;i<tasks.length();i++)if(tasks.optJSONObject(i)!=null&&tasks.optJSONObject(i).optBoolean("done"))done++;
        content.addView(text(tr("کارهای انجام‌شده امروز: ","Tasks completed today: ")+done+" / "+tasks.length(),14));
    }
    void calendarPage(){
        setup(tr("تقویم شمسی","Persian calendar"));
        content.addView(text(tr("تاریخ‌ها بر پایه تقویم هجری شمسی نمایش داده می‌شوند.","Dates are displayed using the Solar Hijri calendar."),15));
        try{
            java.util.Locale persian=java.util.Locale.forLanguageTag("fa-IR-u-ca-persian");
            android.content.res.Configuration config=new android.content.res.Configuration(getResources().getConfiguration());
            config.setLocale(persian);
            Context localized=createConfigurationContext(config);
            DatePicker picker=new DatePicker(localized);
            picker.setCalendarViewShown(true);
            picker.setSpinnersShown(false);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1,-2);
            params.setMargins(0,dp(10),0,dp(10));
            content.addView(picker,params);
            TextView selected=text(tr("تاریخ انتخاب‌شده: ","Selected date: ")+persianDate(),16);
            content.addView(selected);
            picker.init(picker.getYear(),picker.getMonth(),picker.getDayOfMonth(),(view,year,month,day)->{
                java.text.NumberFormat nf=java.text.NumberFormat.getIntegerInstance(persian);
                selected.setText(tr("تاریخ انتخاب‌شده: ","Selected date: ")+nf.format(year)+"/"+nf.format(month+1)+"/"+nf.format(day));
            });
        }catch(Exception e){content.addView(text(persianDate(),18));}
        back();
    }
    void goalPage(){
        setup(tr("اهداف من","My Goals"));content.addView(button("+ "+tr("هدف جدید","New goal"),()->goalDialog()));
        for(int i=0;i<goals.length();i++){final int x=i;JSONObject o=goals.optJSONObject(i);if(o==null)continue;
            String n=o.optString("name"),kind=o.optString("kind");int target=o.optInt("target",1),value=o.optInt("value",0);
            LinearLayout card=card();card.addView(text(n+" • "+(kind.equals("long")?tr("بلندمدت","Long-term"):tr("کوتاه‌مدت","Short-term")),18));
            card.addView(text(value+" / "+target+"  "+tr("پیشرفت","progress"),14));
            ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(Math.max(1,target));bar.setProgress(Math.min(value,target));card.addView(bar);
            LinearLayout row=new LinearLayout(this);row.addView(button("−",()->goalChange(x,-1)));row.addView(button("+",()->goalChange(x,1)));row.addView(button(tr("حذف","Delete"),()->confirmDelete(tr("حذف هدف؟","Delete goal?"),()->{remove(goals,x);save("goals",goals);goalPage();})));card.addView(row);content.addView(card);
        }back();
    }
    void goalDialog(){
        LinearLayout f=form();EditText n=edit(tr("عنوان هدف","Goal title"),0),t=edit(tr("مقدار هدف (عدد)","Target amount (number)"),2);
        Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{tr("کوتاه‌مدت","Short-term"),tr("بلندمدت","Long-term")}));
        f.addView(n);f.addView(t);f.addView(sp);
        new AlertDialog.Builder(this).setTitle(tr("افزودن هدف","Add goal")).setView(f).setNegativeButton(tr("لغو","Cancel"),null).setPositiveButton(tr("ذخیره","Save"),(d,w)->{
            try{if(n.getText().toString().trim().isEmpty())return;int target=Integer.parseInt(t.getText().toString());if(target<1)return;
            JSONObject o=new JSONObject();o.put("name",n.getText().toString().trim());o.put("target",target);o.put("value",0);o.put("kind",sp.getSelectedItemPosition()==1?"long":"short");goals.put(o);save("goals",goals);goalPage();}catch(Exception e){toast(tr("مقدار هدف معتبر نیست","Invalid target"));}}).show();
    }
    void goalChange(int i,int d){JSONObject o=goals.optJSONObject(i);if(o==null)return;try{o.put("value",Math.max(0,o.optInt("value")+d));}catch(Exception e){}save("goals",goals);goalPage();}
    void taskPage(){
        setup(tr("برنامه امروز","Today's Plan"));content.addView(button("+ "+tr("کار جدید","New task"),()->taskDialog()));
        for(int i=0;i<tasks.length();i++){final int x=i;JSONObject o=tasks.optJSONObject(i);if(o==null)continue;LinearLayout c=card();CheckBox cb=new CheckBox(this);cb.setText(o.optString("name"));cb.setTextColor(fg());cb.setChecked(o.optBoolean("done"));cb.setOnCheckedChangeListener((b,v)->{try{o.put("done",v);}catch(Exception e){}save("tasks",tasks);});c.addView(cb);c.addView(button(tr("حذف","Delete"),()->{remove(tasks,x);save("tasks",tasks);taskPage();}));content.addView(c);}
        if(tasks.length()==0)content.addView(text(tr("هنوز کاری ثبت نشده است.","No tasks yet."),15));back();
    }
    void taskDialog(){EditText n=edit(tr("عنوان کار","Task name"),0);new AlertDialog.Builder(this).setTitle(tr("کار جدید","New task")).setView(n).setNegativeButton(tr("لغو","Cancel"),null).setPositiveButton(tr("افزودن","Add"),(d,w)->{String s=n.getText().toString().trim();if(s.isEmpty())return;try{JSONObject o=new JSONObject();o.put("name",s);o.put("done",false);o.put("date",today);tasks.put(o);save("tasks",tasks);taskPage();}catch(Exception e){}}).show();}
    void activityPage(){
        setup(tr("فعالیت‌های من","My Activities"));content.addView(text(tr("فعالیت دلخواهت را بساز؛ مقدارها هر روز از صفر شروع می‌شوند.","Create your own activities; counts reset daily."),14));
        content.addView(button("+ "+tr("افزودن فعالیت","Add activity"),()->activityDialog()));
        for(int i=0;i<activities.length();i++){final int x=i;JSONObject o=activities.optJSONObject(i);if(o==null)continue;LinearLayout c=card();c.addView(text(o.optString("name"),18));c.addView(text(o.optInt("value")+" / "+o.optInt("target")+" "+o.optString("unit"),14));LinearLayout r=new LinearLayout(this);r.addView(button("−",()->activityChange(x,-1)));r.addView(button("+",()->activityChange(x,1)));r.addView(button(tr("حذف","Delete"),()->{remove(activities,x);save("activities",activities);activityPage();}));c.addView(r);content.addView(c);}
        if(activities.length()==0)content.addView(text(tr("هنوز فعالیتی نداری. اولین فعالیتت را اضافه کن.","No activities yet. Add your first one."),15));back();
    }
    void activityDialog(){LinearLayout f=form();EditText n=edit(tr("نام فعالیت","Activity name"),0),u=edit(tr("واحد؛ مثل دقیقه یا صفحه","Unit, e.g. minutes or pages"),0),t=edit(tr("هدف روزانه","Daily target"),2);f.addView(n);f.addView(u);f.addView(t);
        new AlertDialog.Builder(this).setTitle(tr("فعالیت جدید","New activity")).setView(f).setNegativeButton(tr("لغو","Cancel"),null).setPositiveButton(tr("ذخیره","Save"),(d,w)->{try{String name=n.getText().toString().trim(),unit=u.getText().toString().trim();int target=Integer.parseInt(t.getText().toString());if(name.isEmpty()||unit.isEmpty()||target<1)return;JSONObject o=new JSONObject();o.put("name",name);o.put("unit",unit);o.put("target",target);o.put("value",0);activities.put(o);save("activities",activities);activityPage();}catch(Exception e){toast(tr("اطلاعات را بررسی کن","Check the details"));}}).show();}
    void activityChange(int i,int d){JSONObject o=activities.optJSONObject(i);if(o==null)return;try{o.put("value",Math.max(0,o.optInt("value")+d));}catch(Exception e){}save("activities",activities);activityPage();}
    void financePage(){
        setup(tr("امور مالی","Finance"));int income=0,expense=0;
        for(int i=0;i<transactions.length();i++){JSONObject o=transactions.optJSONObject(i);if(o==null)continue;if(o.optString("type").equals("income"))income+=o.optInt("amount");else expense+=o.optInt("amount");}
        content.addView(text(tr("موجودی خالص ثبت‌شده: ","Recorded net balance: ")+money(income-expense)+" "+tr("تومان","Toman"),19));
        content.addView(text(tr("درآمد: ","Income: ")+money(income)+"   "+tr("هزینه: ","Expense: ")+money(expense),14));
        content.addView(button("+ "+tr("ثبت تراکنش","Add transaction"),()->transactionDialog()));
        content.addView(button("+ "+tr("ثبت بدهی یا طلب","Add debt / credit"),()->debtDialog()));
        content.addView(button("+ "+tr("ثبت دارایی","Add asset"),()->assetDialog()));
        heading(tr("تراکنش‌ها","Transactions"));
        for(int i=transactions.length()-1;i>=0;i--){final int x=i;JSONObject o=transactions.optJSONObject(i);if(o==null)continue;content.addView(line(o.optString("name")+" • "+money(o.optInt("amount"))+" "+tr("تومان","Toman")+" • "+(o.optString("type").equals("income")?tr("درآمد","Income"):tr("هزینه","Expense")),()->{remove(transactions,x);save("transactions",transactions);financePage();}));}
        heading(tr("بدهی‌ها و طلب‌ها","Debts and credits"));
        for(int i=0;i<debts.length();i++){final int x=i;JSONObject o=debts.optJSONObject(i);if(o!=null)content.addView(line(o.optString("name")+" • "+money(o.optInt("amount"))+" • "+o.optString("type"),()->{remove(debts,x);save("debts",debts);financePage();}));}
        heading(tr("دارایی‌ها","Assets"));
        for(int i=0;i<assets.length();i++){final int x=i;JSONObject o=assets.optJSONObject(i);if(o!=null)content.addView(line(o.optString("name")+" • "+money(o.optInt("amount")),()->{remove(assets,x);save("assets",assets);financePage();}));}
        back();
    }
    void transactionDialog(){LinearLayout f=form();EditText n=edit(tr("شرح","Description"),0),a=edit(tr("مبلغ به تومان","Amount in toman"),2);Spinner s=new Spinner(this);s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{tr("هزینه","Expense"),tr("درآمد","Income")}));f.addView(n);f.addView(a);f.addView(s);
        new AlertDialog.Builder(this).setTitle(tr("تراکنش جدید","New transaction")).setView(f).setNegativeButton(tr("لغو","Cancel"),null).setPositiveButton(tr("ذخیره","Save"),(d,w)->{try{int v=Integer.parseInt(a.getText().toString());if(v<1||n.getText().toString().trim().isEmpty())return;JSONObject o=new JSONObject();o.put("name",n.getText().toString().trim());o.put("amount",v);o.put("type",s.getSelectedItemPosition()==1?"income":"expense");o.put("date",today);transactions.put(o);save("transactions",transactions);financePage();}catch(Exception e){toast(tr("مبلغ معتبر نیست","Invalid amount"));}}).show();}
    void debtDialog(){LinearLayout f=form();EditText n=edit(tr("شرح بدهی یا طلب","Description"),0),a=edit(tr("مبلغ به تومان","Amount in toman"),2);Spinner s=new Spinner(this);s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{tr("بدهی من","I owe"),tr("طلب من","Owed to me"),tr("وام / قسط","Loan / installment")}));f.addView(n);f.addView(a);f.addView(s);new AlertDialog.Builder(this).setTitle(tr("بدهی یا طلب","Debt / credit")).setView(f).setNegativeButton(tr("لغو","Cancel"),null).setPositiveButton(tr("ذخیره","Save"),(d,w)->{try{JSONObject o=new JSONObject();o.put("name",n.getText().toString());o.put("amount",Integer.parseInt(a.getText().toString()));o.put("type",s.getSelectedItem().toString());debts.put(o);save("debts",debts);financePage();}catch(Exception e){toast(tr("اطلاعات را بررسی کن","Check details"));}}).show();}
    void assetDialog(){LinearLayout f=form();EditText n=edit(tr("نام دارایی","Asset name"),0),a=edit(tr("ارزش به تومان","Value in toman"),2);f.addView(n);f.addView(a);new AlertDialog.Builder(this).setTitle(tr("دارایی جدید","New asset")).setView(f).setNegativeButton(tr("لغو","Cancel"),null).setPositiveButton(tr("ذخیره","Save"),(d,w)->{try{JSONObject o=new JSONObject();o.put("name",n.getText().toString());o.put("amount",Integer.parseInt(a.getText().toString()));assets.put(o);save("assets",assets);financePage();}catch(Exception e){toast(tr("اطلاعات را بررسی کن","Check details"));}}).show();}
    void reports(){setup(tr("گزارش‌ها","Reports"));int done=0;for(int i=0;i<tasks.length();i++){JSONObject o=tasks.optJSONObject(i);if(o!=null&&o.optBoolean("done"))done++;}content.addView(text(tr("گزارش امروز","Today's report"),19));content.addView(text(tr("کارهای انجام‌شده: ","Completed tasks: ")+done+" / "+tasks.length(),16));content.addView(text(tr("اهداف ثبت‌شده: ","Goals created: ")+goals.length(),16));content.addView(text(tr("فعالیت‌های شخصی: ","Custom activities: ")+activities.length(),16));int income=0,expense=0;for(int i=0;i<transactions.length();i++){JSONObject o=transactions.optJSONObject(i);if(o!=null){if(o.optString("type").equals("income"))income+=o.optInt("amount");else expense+=o.optInt("amount");}}content.addView(text(tr("درآمد کل: ","Total income: ")+money(income)+" "+tr("تومان","Toman"),16));content.addView(text(tr("هزینه کل: ","Total expenses: ")+money(expense)+" "+tr("تومان","Toman"),16));back();}
    void settings(){setup(tr("تنظیمات","Settings"));content.addView(button(tr("زبان: فارسی / English","Language: Persian / English"),()->{lang=lang.equals("fa")?"en":"fa";prefs.edit().putString("language",lang).apply();settings();}));content.addView(button(tr("تغییر تم روشن / تاریک","Toggle light / dark theme"),()->{dark=!dark;prefs.edit().putBoolean("dark",dark).apply();settings();}));content.addView(button(tr("پشتیبان‌گیری","Backup data"),()->backup()));content.addView(button(tr("حذف همه اطلاعات","Delete all data"),()->confirmDelete(tr("تمام اطلاعات پاک شود؟","Delete all data?"),()->{prefs.edit().clear().apply();goals=new JSONArray();tasks=new JSONArray();activities=new JSONArray();transactions=new JSONArray();debts=new JSONArray();assets=new JSONArray();lang="fa";dark=false;home();})));content.addView(text(tr("اطلاعات فقط روی همین گوشی ذخیره می‌شود.","Your data is stored locally on this phone."),14));back();}
    void backup(){try{JSONObject all=new JSONObject();all.put("goals",goals);all.put("tasks",tasks);all.put("activities",activities);all.put("transactions",transactions);all.put("debts",debts);all.put("assets",assets);Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/plain");send.putExtra(Intent.EXTRA_SUBJECT,"Goal backup");send.putExtra(Intent.EXTRA_TEXT,all.toString(2));startActivity(Intent.createChooser(send,tr("ارسال نسخه پشتیبان","Share backup")));}catch(Exception e){toast("Backup failed");}}
    LinearLayout card(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(1);c.setPadding(dp(14),dp(12),dp(14),dp(12));
        c.setBackground(shape(dark?Color.rgb(36,43,83):Color.WHITE,dark?Color.rgb(25,39,78):Color.rgb(226,234,255),Color.argb(100,120,135,235)));
        c.setElevation(dp(4));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(7),0,dp(7));c.setLayoutParams(p);return c;
    }
    LinearLayout form(){LinearLayout f=new LinearLayout(this);f.setOrientation(1);f.setPadding(dp(12),dp(4),dp(12),dp(4));return f;}
    View line(String s,Runnable r){TextView t=text(s+"     ×",15);t.setBackground(shape(dark?Color.rgb(35,43,79):Color.WHITE,dark?Color.rgb(26,36,70):Color.rgb(230,237,255),Color.argb(90,120,135,235)));t.setElevation(dp(2));t.setOnClickListener(v->r.run());LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(4),0,dp(4));t.setLayoutParams(p);return t;}
    void remove(JSONArray a,int index){JSONArray b=new JSONArray();for(int i=0;i<a.length();i++)if(i!=index)b.put(a.opt(i));if(a==goals)goals=b;else if(a==tasks)tasks=b;else if(a==activities)activities=b;else if(a==transactions)transactions=b;else if(a==debts)debts=b;else if(a==assets)assets=b;}
    void confirmDelete(String title,Runnable r){new AlertDialog.Builder(this).setMessage(title).setNegativeButton(tr("انصراف","Cancel"),null).setPositiveButton(tr("حذف","Delete"),(d,w)->r.run()).show();}
    String money(int n){return String.format(Locale.US,"%,d",n);}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
