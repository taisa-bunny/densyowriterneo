package fbc.DensyoWriterNeo;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import fbc.DensyoWriterNeo.R;

public class MainActivity extends ActionBarActivity {

//変数宣言部	開始
	
	private ArrayList<String> item_list;
	private String strPath;
	private File[] files;
	private ListView listview;
	private String strItemPath; //リスト上で選択されたItemのパス
	
//変数宣言部　終了
	
/*
 * 処理名：mkdir
 * 概要　：DensyoWriterフォルダを作成
 * 引数　：path(mnt/sdcard/)
 * 戻り値：Ture/False
 * 作成　：2014/09/11
 */
	
	public boolean mkdir(String path){
		File file = new File(path);
		return file.mkdir();
	}
	
/*
 * 処理名：onCreate
 * 概要　：レイアウトの読み込み
 * 引数　：Bundle
 * 戻り値：none
 * 作成　：2014/09/11
 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//属性 android:id="@+id/mainLayout"が与えられているものとしてRelativeLayoutをルックアップする
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.mainLayout);
		
		//リスト表示
		showList();
	}

/*
 * 処理名：showList
 * 概要　：リスト表示処理
 * 引数　：none
 * 戻り値：none
 * 作成　：2014/09/11
 */
	
	public void showList() {
	
		//レイアウトのオブジェクト取得
		listview = (ListView)findViewById(R.id.listview);
		
		//ファイル文字列格納用リスト
		item_list = new ArrayList<String>();
		
		//ファイルを置くディレクトリパスを取得する
		File file = null;
		file = Environment.getDataDirectory();
		files = new File(file.getPath()).listFiles();
	
		//SDカード内のディレクトリを表示
		strPath = getExternalStoragePath() + "DensyoWriter";
		boolean strOpath = mkdir(strPath);
		files = new File(strPath).listFiles();
		
		//カレントディレクトリ内のディレクトリを表示（隠しファイル以外を表示）
		if(files != null){
			for(int i=0; i < files.length; i++){
				if(files[i].isHidden()!=true
						&& files[i].isDirectory()==false){
					item_list.add(files[i].getName());
				}
			}			
		}
		
		//アダプターの作成
		ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,item_list);
		
		//アダプターにリストビューをセット
		listview.setAdapter(arrayadapter);
		
		//リストビューのアイテムがクリックされた時に呼び出されるクリックリスナーの登録（クリック時の動作）
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ListView listView = (ListView) parent;
				
				//クリックされたアイテムを取得
				String item = (String) listView.getItemAtPosition(position);
				
				//アイテムまでのフルパスを取得
				strItemPath = strPath + "/" + item;
				
				//アイテムのフルパスを使用してFileとして読み込む
				File file = new File(strItemPath);
				
				
				if(file.exists()){
					try{
						FileInputStream fiStream = new FileInputStream(file);
						byte[] readBytes = new byte[fiStream.available()];
						fiStream.read(readBytes);
						
						//取得できる文字列
						Intent intent = new Intent();
						intent.setClassName(
								"fbc.DensyoWriterNeo", 
								"fbc.DensyoWriterNeo.WriteActivity");
						
						//.txtを除去
						int index = item.indexOf(".txt");
						
						//タイトルと本文をWriteActivityに渡す
						intent.putExtra("title", item.substring(0, index));
						intent.putExtra("sentence", new String(readBytes));
						startActivity(intent);
						
						fiStream.close();
						
					} catch (Exception e) {						
					}
				} else {
					//存在しない			
				}
			}			
		});
	
		listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				ListView listView = (ListView) parent;
				String item = (String) listView.getItemAtPosition(position);
				
				//アイテムまでのフルパスを取得
				strItemPath = strPath + "/" + item;
				
				//アラーとダイアログの設定をしておく
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
				
				//ダイアログの設定
				alertDialog.setIcon(R.drawable.ic_launcher); //アイコン設定
				alertDialog.setTitle("作品削除");				 //タイトル設定
				alertDialog.setMessage("作品を削除します。よろしいですか？"); //内容設定
				
				//OK設定
				alertDialog.setPositiveButton("削除する", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						//OK押下の処理
						Log.d("AlartDialog", "Positive which :" + which);
						
						//削除処理
						deleteFile(strItemPath);
						
						//リスト再表示
						showList();	
					}
				});
				
				//NG設定
				alertDialog.setNegativeButton("しないしない！", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//NG押下の処理
						Log.d("AlartDialog", "Negative which :" + which);
						
					}
				});
				
				//ダイアログ表示
				alertDialog.show();
				
				return false;
			}
			
		});
		
	}
	
/*
 * 処理名：getExternalStoragePath
 * 概要　：外部SDカードのパスを取得する
 * 引数　：none
 * 戻り値：path
 * 作成　：2014/09/11
 */	

	public static String getExternalStoragePath(){
		String path = "";
		
		//MOTOROLA対応
		path = System.getenv("EXTERNAL_ALT_STORAGE");
		
		//Sumsung対応
		path = System.getenv("EXTERNAL_STRAGE2");
		if(path != null)
			return path;
		
		//旧Sumsung + 標準対応
		path = System.getenv("EXTERNAL_STRAGE");
		if(path == null)
			path = Environment.getExternalStorageDirectory().getPath();
		
		//HTC対応
		File file = new File(path + "/ext_sd");
		if(file.exists())
			path = file.getPath();
		
		//その他機種対応
		return path;
		
	}
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
