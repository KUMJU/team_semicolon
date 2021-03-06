package com.semicolon.project.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    DBManager db;
    ArrayAdapter<HashMap<String,String>> adapter;
    HashMap<String,String> hashMap=null;

    Toolbar ListToolbar;

    ArrayList <HashMap<String,String>> theList = new ArrayList<>();
    static final String TAG_ID= "ID";
    static final String TAG_NAME="NAME";
    static final String TAG_MEMO="MEMO";
    static final String TAG_DATE="DATE";
    static final String TAG_VALUE="VALUE";
    static SimpleAdapter simpleAdapter;
    static Integer cursor_integer = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //리스트 레이아웃 툴바
        ListToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(ListToolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("보관함");
        ListToolbar.setTitleTextColor(Color.WHITE);

        db = new DBManager(this);
        sort_List();

        final ListView lv = (ListView) findViewById(R.id.listview);
        //adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, theList);
        simpleAdapter= new SimpleAdapter(this,theList,R.layout.list_item,new String[]{TAG_NAME,TAG_MEMO,TAG_DATE,TAG_VALUE},
                new int[]{R.id.NAME,R.id.MEMO,R.id.DATE,R.id.VALUE});
        lv.setAdapter(simpleAdapter);

        //리스트 터치
        lv.setClickable ( true );
        lv.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView <?> parent , View view , int position , long id) {
               HashMap selection = (HashMap)lv.getItemAtPosition ( position );
                final String d_name=(String)selection.get(TAG_NAME);

                CharSequence info[]= new CharSequence[]{"레시피보기","삭제하기"};

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder (ListActivity.this);
                alertDialog.setTitle ( "리스트 옵션" );
                alertDialog.setItems( info, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int whichButton){
                        switch (whichButton){
                            case 0:
                                Uri webpage = Uri.parse("https://www.google.co.kr/search?q=" + d_name + " 레시피");
                                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                startActivity(webIntent);
                                break;
                            case 1:
                                db.deleteData (d_name);
                                sort_List();
                                simpleAdapter.notifyDataSetChanged();
                                Toast.makeText ( getApplicationContext (),"삭제되었습니다.",Toast.LENGTH_LONG ).show ();
                        }
                        dialog.dismiss ();
                    }
                });
                AlertDialog al = alertDialog.create ();
                al.show ();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_settings:
                // 임박순 정렬
                cursor_integer = 1;
                sort_List();
                simpleAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "임박순 정렬", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_settings2:
                //등록순 정렬
                cursor_integer = 0;
                sort_List();
                simpleAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "등록순 정렬", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_settings3:
                //이름순 정렬
                cursor_integer = 2;
                sort_List();
                simpleAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "이름순 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_delete:
                //삭제 버튼
                db.DeleteAlldata();
                theList.clear();
                simpleAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "전체삭제 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void sort_List() {
        theList.clear();

        Cursor data;

        switch (cursor_integer) {
            case 0 :
                data = db.getListContents();
                break;
            case 1 :
                data = db.sort_Date();
                break;
            case 2 :
                data = db.sort_Name();
                break;
            default:
                data = db.getListContents();
        }

        if (data.getCount() == 0) {
            Toast.makeText(ListActivity.this, "저장된 상품이 없습니다", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                hashMap = new HashMap<String, String>();

                hashMap.put(TAG_NAME, data.getString(1));
                hashMap.put(TAG_MEMO, data.getString(2));
                hashMap.put(TAG_DATE, data.getString(3));
                hashMap.put(TAG_VALUE, data.getString(4));

                theList.add(hashMap);
            }
        }
    }
}
