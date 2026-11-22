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
import java.net.*;
import java.io.*;

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
        dark=prefs.getBoolean("dark",true);
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
        setup("GOAL");
        int done=0;for(int i=0;i<tasks.length();i++){JSONObject o=tasks.optJSONObject(i);if(o!=null&&o.optBoolean("done"))done++;}
        int goalDone=0,goalTotal=0;for(int i=0;i<goals.length();i++){JSONObject o=goals.optJSONObject(i);if(o!=null){goalTotal+=Math.max(1,o.optInt("target",1));goalDone+=Math.min(Math.max(0,o.optInt("value",0)),Math.max(1,o.optInt("target",1)));}}
        int percent=goalTotal>0?(int)(100f*goalDone/goalTotal):(tasks.length()>0?(int)(100f*done/tasks.length()):0);
        LinearLayout top=new LinearLayout(this);top.setOrientation(0);top.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout brand=new LinearLayout(this);brand.setOrientation(1);brand.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
        TextView over=text("✦  GOAL / PERSONAL GROWTH",10);over.setTextColor(cyan);over.setTypeface(null,android.graphics.Typeface.BOLD);
        TextView welcome=text(tr("سلام، به فضای پیشرفتت خوش اومدی","Welcome to your growth space"),21);welcome.setTypeface(null,android.graphics.Typeface.BOLD);
        TextView dateText=text(persianDate(),12);dateText.setTextColor(dark?Color.rgb(159,174,213):Color.GRAY);
        brand.addView(over);brand.addView(welcome);brand.addView(dateText);top.addView(brand);
        TextView avatar=text("G",20);avatar.setTextColor(Color.WHITE);avatar.setGravity(Gravity.CENTER);avatar.setTypeface(null,android.graphics.Typeface.BOLD);avatar.setBackground(shape(purple,blue,Color.argb(170,220,220,255)));
        LinearLayout.LayoutParams avp=new LinearLayout.LayoutParams(dp(48),dp(48));avatar.setLayoutParams(avp);top.addView(avatar);content.addView(top);
        LinearLayout hero=card();hero.setPadding(dp(18),dp(18),dp(18),dp(18));
        hero.setBackground(shape(Color.rgb(42,39,105),Color.rgb(22,65,119),Color.argb(150,116,140,255)));
        TextView heroTag=text(tr("نمای کلی عملکرد","YOUR OVERVIEW"),11);heroTag.setTextColor(Color.rgb(179,196,255));heroTag.setTypeface(null,android.graphics.Typeface.BOLD);hero.addView(heroTag);
        LinearLayout ringRow=new LinearLayout(this);ringRow.setGravity(Gravity.CENTER_VERTICAL);ringRow.setOrientation(0);
        ProgressRing ring=new ProgressRing(this,percent);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(dp(124),dp(124));rp.setMargins(dp(4),dp(8),dp(18),dp(4));ringRow.addView(ring,rp);
        LinearLayout summary=new LinearLayout(this);summary.setOrientation(1);summary.setGravity(Gravity.CENTER_VERTICAL);
        TextView pct=text(percent+"%",29);pct.setTextColor(Color.WHITE);pct.setTypeface(null,android.graphics.Typeface.BOLD);pct.setPadding(0,0,0,dp(2));
        TextView cap=text(tr("میزان پیشرفت کلی","OVERALL PROGRESS"),10);cap.setTextColor(Color.rgb(194,207,255));cap.setPadding(0,0,0,dp(9));
        TextView quote=text(dailyMotivation,13);quote.setTextColor(Color.WHITE);quote.setPadding(0,0,0,0);
        summary.addView(pct);summary.addView(cap);summary.addView(quote);ringRow.addView(summary,new LinearLayout.LayoutParams(0,-2,1));hero.addView(ringRow);
        content.addView(hero);
        TextView quick=text(tr("خلاصه امروز","TODAY AT A GLANCE"),13);quick.setTypeface(null,android.graphics.Typeface.BOLD);quick.setTextColor(cyan);content.addView(quick);
        LinearLayout stats=new LinearLayout(this);stats.setOrientation(0);
        stats.addView(statCard("◎",String.valueOf(goals.length()),tr("هدف فعال","GOALS")),new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(statCard("✓",done+"/"+tasks.length(),tr("کار انجام‌شده","TASKS")),new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(statCard("✧",String.valueOf(activities.length()),tr("فعالیت شخصی","TRACKERS")),new LinearLayout.LayoutParams(0,-2,1));
        content.addView(stats);
        TextView section=text(tr("فضای کاری تو","YOUR WORKSPACE"),13);section.setTypeface(null,android.graphics.Typeface.BOLD);section.setTextColor(cyan);content.addView(section);
        LinearLayout row1=new LinearLayout(this);row1.setOrientation(0);
        row1.addView(navItem("◎",tr("اهداف من","My Goals"),tr("مسیر رشد و دستاوردها","Milestones"),()->goalPage()),new LinearLayout.LayoutParams(0,-2,1));
        row1.addView(navItem("▤",tr("برنامه امروز","Today"),tr("اولویت‌های روزانه","Daily priorities"),()->taskPage()),new LinearLayout.LayoutParams(0,-2,1));content.addView(row1);
        LinearLayout row2=new LinearLayout(this);row2.setOrientation(0);
        row2.addView(navItem("✧",tr("فعالیت‌ها","Activities"),tr("ردیابی دلخواه","Your trackers"),()->activityPage()),new LinearLayout.LayoutParams(0,-2,1));
        row2.addView(navItem("◷",tr("تقویم شمسی","Calendar"),tr("برنامه‌ریزی ایرانی","Persian calendar"),()->calendarPage()),new LinearLayout.LayoutParams(0,-2,1));content.addView(row2);
        LinearLayout row3=new LinearLayout(this);row3.setOrientation(0);
        row3.addView(navItem("◈",tr("امور مالی","Finance"),tr("مدیریت پول","Money manager"),()->financePage()),new LinearLayout.LayoutParams(0,-2,1));
        row3.addView(navItem("↗",tr("بازار و قیمت‌ها","Market"),tr("دلار و طلا","USD & Gold"),()->marketPage()),new LinearLayout.LayoutParams(0,-2,1));content.addView(row3);
        LinearLayout row4=new LinearLayout(this);row4.setOrientation(0);
        row4.addView(navItem("▥",tr("گزارش‌ها","Reports"),tr("تحلیل پیشرفت","Insights"),()->reports()),new LinearLayout.LayoutParams(0,-2,1));
        row4.addView(navItem("⚙",tr("تنظیمات","Settings"),tr("شخصی‌سازی","Preferences"),()->settings()),new LinearLayout.LayoutParams(0,-2,1));content.addView(row4);
    }
    LinearLayout statCard(String glyph,String value,String label){
        LinearLayout c=card();c.setPadding(dp(8),dp(10),dp(8),dp(10));c.setGravity(Gravity.CENTER);
        TextView g=text(glyph,19);g.setTextColor(cyan);g.setGravity(Gravity.CENTER);g.setPadding(0,0,0,dp(3));
        TextView v=text(value,19);v.setTextColor(Color.WHITE);v.setTypeface(null,android.graphics.Typeface.BOLD);v.setGravity(Gravity.CENTER);v.setPadding(0,0,0,dp(2));
        TextView l=text(label,9);l.setTextColor(dark?Color.rgb(172,186,224):Color.GRAY);l.setGravity(Gravity.CENTER);l.setPadding(0,0,0,0);
        c.addView(g);c.addView(v);c.addView(l);return c;
    }
    class ProgressRing extends View{
        android.graphics.Paint p=new android.graphics.Paint(3);int value;
        ProgressRing(Context c,int v){super(c);value=Math.max(0,Math.min(100,v));setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        @Override protected void onDraw(android.graphics.Canvas canvas){super.onDraw(canvas);float cx=getWidth()/2f,cy=getHeight()/2f,r=Math.min(cx,cy)-dp(12);
            p.setStyle(android.graphics.Paint.Style.STROKE);p.setStrokeWidth(dp(9));p.setStrokeCap(android.graphics.Paint.Cap.ROUND);p.setColor(Color.argb(65,220,225,255));canvas.drawCircle(cx,cy,r,p);
            p.setShader(new android.graphics.LinearGradient(0,0,getWidth(),getHeight(),cyan,purple,android.graphics.Shader.TileMode.CLAMP));canvas.drawArc(cx-r,cy-r,cx+r,cy+r,-90,360f*value/100,false,p);p.setShader(null);
            p.setStyle(android.graphics.Paint.Style.FILL);p.setColor(Color.WHITE);p.setTextAlign(android.graphics.Paint.Align.CENTER);p.setTypeface(android.graphics.Typeface.create("sans-serif-medium",1));p.setTextSize(dp(24));canvas.drawText(value+"%",cx,cy+dp(8),p);
        }
    }

    LinearLayout navItem(String glyph,String title,String subtitle,Runnable action){
        LinearLayout outer=new LinearLayout(this);outer.setOrientation(1);outer.setGravity(Gravity.CENTER_VERTICAL);
        outer.setPadding(dp(12),dp(12),dp(12),dp(12));outer.setBackground(shape(Color.rgb(29,36,76),Color.rgb(20,49,83),Color.argb(100,104,125,220)));outer.setElevation(dp(3));
        LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(0,-2,1);op.setMargins(dp(3),dp(5),dp(3),dp(5));outer.setLayoutParams(op);
        TextView icon=new TextView(this);icon.setText(glyph);icon.setTextSize(24);icon.setTypeface(null,android.graphics.Typeface.BOLD);icon.setTextColor(Color.WHITE);icon.setGravity(Gravity.CENTER);icon.setBackground(shape(purple,blue,Color.argb(150,220,225,255)));
        LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(45),dp(45));ip.setMargins(0,0,0,dp(8));icon.setLayoutParams(ip);outer.addView(icon);
        TextView name=text(title,14);name.setTypeface(null,android.graphics.Typeface.BOLD);name.setTextColor(Color.WHITE);name.setPadding(0,0,0,dp(2));
        TextView detail=text(subtitle,10);detail.setTextColor(Color.rgb(161,178,218));detail.setPadding(0,0,0,0);
        outer.addView(name);outer.addView(detail);outer.setOnClickListener(v->action.run());return outer;
    }
    void marketPage(){
        setup(tr("بازار و قیمت‌های روز","Market & Live Rates"));
        content.addView(text(tr("نرخ‌های بازار آزاد ایران، به تومان","Iran open-market rates in toman"),14));
        LinearLayout status=card();TextView statusText=text(tr("در حال دریافت آخرین نرخ‌ها…","Fetching latest rates…"),13);status.addView(statusText);content.addView(status);
        LinearLayout rates=new LinearLayout(this);rates.setOrientation(1);content.addView(rates);
        content.addView(button(tr("↻  به‌روزرسانی قیمت‌ها","↻  Refresh prices"),()->loadMarketRates(rates,statusText)));
        content.addView(text(tr("منبع: Iran Market Data • انتشار داده تقریباً هر ۳۰ دقیقه","Source: Iran Market Data • published about every 30 minutes"),11));
        content.addView(text(tr("قیمت‌ها صرفاً جهت اطلاع‌اند و توصیه خرید یا فروش نیستند.","Prices are informational only, not investment advice."),11));
        back();loadMarketRates(rates,statusText);
    }
    void loadMarketRates(LinearLayout rates,TextView statusText){
        statusText.setText(tr("در حال دریافت نرخ‌ها…","Loading market rates…"));
        new Thread(()->{
            HttpURLConnection connection=null;
            try{
                URL url=new URL("https://raw.githubusercontent.com/iran-market/iran-market.github.io/main/data/popular.json");
                connection=(HttpURLConnection)url.openConnection();connection.setRequestMethod("GET");connection.setConnectTimeout(12000);connection.setReadTimeout(12000);connection.setRequestProperty("Accept","application/json");
                StringBuilder body=new StringBuilder();
                try(BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"))){String line;while((line=reader.readLine())!=null)body.append(line);}
                JSONObject payload=new JSONObject(body.toString());JSONArray data=payload.optJSONArray("data");JSONObject usd=null,gold=null,coin=null;
                if(data!=null)for(int i=0;i<data.length();i++){JSONObject item=data.optJSONObject(i);if(item==null)continue;String symbol=item.optString("symbol");if(symbol.equals("USD_IRR_FREE"))usd=item;else if(symbol.equals("GOLD_18K_IRR"))gold=item;else if(symbol.equals("COIN_EMAMI_IRR"))coin=item;}
                final JSONObject u=usd,g=gold,c=coin;
                runOnUiThread(()->{rates.removeAllViews();if(u==null||g==null){statusText.setText(tr("داده معتبر دریافت نشد؛ دوباره تلاش کن.","No valid data received. Please retry."));return;}
                    addMarketCard(rates,"＄",tr("دلار آمریکا • بازار آزاد","US Dollar • Open Market"),u);
                    addMarketCard(rates,"Au",tr("طلای ۱۸ عیار • هر گرم","18K Gold • per gram"),g);
                    if(c!=null)addMarketCard(rates,"◇",tr("سکه امامی","Emami Coin"),c);
                    statusText.setText(tr("آخرین زمان ثبت داده: ","Latest data timestamp: ")+u.optString("timestamp_jalali",u.optString("timestamp","نامشخص")));
                });
            }catch(Exception e){runOnUiThread(()->statusText.setText(tr("اتصال برقرار نشد. اینترنت را بررسی و دوباره تلاش کن.","Connection failed. Check internet and retry.")));}
            finally{if(connection!=null)connection.disconnect();}
        }).start();
    }
    void addMarketCard(LinearLayout target,String symbol,String label,JSONObject item){
        LinearLayout c=card();LinearLayout top=new LinearLayout(this);top.setOrientation(0);top.setGravity(Gravity.CENTER_VERTICAL);
        TextView emblem=text(symbol,20);emblem.setGravity(Gravity.CENTER);emblem.setTextColor(Color.WHITE);emblem.setBackground(shape(purple,blue,Color.argb(120,220,225,255)));
        LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(dp(48),dp(48));ep.setMargins(0,0,dp(12),0);emblem.setLayoutParams(ep);top.addView(emblem);
        LinearLayout labels=new LinearLayout(this);labels.setOrientation(1);TextView title=text(label,14);title.setTypeface(null,android.graphics.Typeface.BOLD);title.setPadding(0,0,0,dp(3));labels.addView(title);
        TextView price=text(moneyLong(item.optLong("price"))+" "+tr("تومان","Toman"),21);price.setTextColor(dark?Color.rgb(190,177,255):Color.rgb(83,71,190));price.setTypeface(null,android.graphics.Typeface.BOLD);price.setPadding(0,0,0,0);labels.addView(price);top.addView(labels);c.addView(top);
        double change=item.optDouble("change_pct",0);TextView movement=text(tr("تغییر نسبت به نرخ قبلی: ","Change vs previous: ")+String.format(Locale.US,"%+.2f%%",change),12);
        movement.setTextColor(change>0?Color.rgb(30,170,115):(change<0?Color.rgb(225,105,105):fg()));c.addView(movement);target.addView(c);
    }
    String moneyLong(long n){return String.format(Locale.US,"%,d",n);}

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
