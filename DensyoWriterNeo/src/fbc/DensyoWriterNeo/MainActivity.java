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

//�ϐ��錾��	�J�n
	
	private ArrayList<String> item_list;
	private String strPath;
	private File[] files;
	private ListView listview;
	private String strItemPath; //���X�g��őI�����ꂽItem�̃p�X
	
//�ϐ��錾���@�I��
	
/*
 * �������Fmkdir
 * �T�v�@�FDensyoWriter�t�H���_���쐬
 * �����@�Fpath(mnt/sdcard/)
 * �߂�l�FTure/False
 * �쐬�@�F2014/09/11
 */
	
	public boolean mkdir(String path){
		File file = new File(path);
		return file.mkdir();
	}
	
/*
 * �������FonCreate
 * �T�v�@�F���C�A�E�g�̓ǂݍ���
 * �����@�FBundle
 * �߂�l�Fnone
 * �쐬�@�F2014/09/11
 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//���� android:id="@+id/mainLayout"���^�����Ă�����̂Ƃ���RelativeLayout�����b�N�A�b�v����
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.mainLayout);
		
		//���X�g�\��
		showList();
	}

/*
 * �������FshowList
 * �T�v�@�F���X�g�\������
 * �����@�Fnone
 * �߂�l�Fnone
 * �쐬�@�F2014/09/11
 */
	
	public void showList() {
	
		//���C�A�E�g�̃I�u�W�F�N�g�擾
		listview = (ListView)findViewById(R.id.listview);
		
		//�t�@�C��������i�[�p���X�g
		item_list = new ArrayList<String>();
		
		//�t�@�C����u���f�B���N�g���p�X���擾����
		File file = null;
		file = Environment.getDataDirectory();
		files = new File(file.getPath()).listFiles();
	
		//SD�J�[�h���̃f�B���N�g����\��
		strPath = getExternalStoragePath() + "DensyoWriter";
		boolean strOpath = mkdir(strPath);
		files = new File(strPath).listFiles();
		
		//�J�����g�f�B���N�g�����̃f�B���N�g����\���i�B���t�@�C���ȊO��\���j
		if(files != null){
			for(int i=0; i < files.length; i++){
				if(files[i].isHidden()!=true
						&& files[i].isDirectory()==false){
					item_list.add(files[i].getName());
				}
			}			
		}
		
		//�A�_�v�^�[�̍쐬
		ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,item_list);
		
		//�A�_�v�^�[�Ƀ��X�g�r���[���Z�b�g
		listview.setAdapter(arrayadapter);
		
		//���X�g�r���[�̃A�C�e�����N���b�N���ꂽ���ɌĂяo�����N���b�N���X�i�[�̓o�^�i�N���b�N���̓���j
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ListView listView = (ListView) parent;
				
				//�N���b�N���ꂽ�A�C�e�����擾
				String item = (String) listView.getItemAtPosition(position);
				
				//�A�C�e���܂ł̃t���p�X���擾
				strItemPath = strPath + "/" + item;
				
				//�A�C�e���̃t���p�X���g�p����File�Ƃ��ēǂݍ���
				File file = new File(strItemPath);
				
				
				if(file.exists()){
					try{
						FileInputStream fiStream = new FileInputStream(file);
						byte[] readBytes = new byte[fiStream.available()];
						fiStream.read(readBytes);
						
						//�擾�ł��镶����
						Intent intent = new Intent();
						intent.setClassName(
								"fbc.DensyoWriterNeo", 
								"fbc.DensyoWriterNeo.WriteActivity");
						
						//.txt������
						int index = item.indexOf(".txt");
						
						//�^�C�g���Ɩ{����WriteActivity�ɓn��
						intent.putExtra("title", item.substring(0, index));
						intent.putExtra("sentence", new String(readBytes));
						startActivity(intent);
						
						fiStream.close();
						
					} catch (Exception e) {						
					}
				} else {
					//���݂��Ȃ�			
				}
			}			
		});
	
		listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				ListView listView = (ListView) parent;
				String item = (String) listView.getItemAtPosition(position);
				
				//�A�C�e���܂ł̃t���p�X���擾
				strItemPath = strPath + "/" + item;
				
				//�A���[�ƃ_�C�A���O�̐ݒ�����Ă���
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
				
				//�_�C�A���O�̐ݒ�
				alertDialog.setIcon(R.drawable.ic_launcher); //�A�C�R���ݒ�
				alertDialog.setTitle("��i�폜");				 //�^�C�g���ݒ�
				alertDialog.setMessage("��i���폜���܂��B��낵���ł����H"); //���e�ݒ�
				
				//OK�ݒ�
				alertDialog.setPositiveButton("�폜����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						//OK�����̏���
						Log.d("AlartDialog", "Positive which :" + which);
						
						//�폜����
						deleteFile(strItemPath);
						
						//���X�g�ĕ\��
						showList();	
					}
				});
				
				//NG�ݒ�
				alertDialog.setNegativeButton("���Ȃ����Ȃ��I", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//NG�����̏���
						Log.d("AlartDialog", "Negative which :" + which);
						
					}
				});
				
				//�_�C�A���O�\��
				alertDialog.show();
				
				return false;
			}
			
		});
		
	}
	
/*
 * �������FgetExternalStoragePath
 * �T�v�@�F�O��SD�J�[�h�̃p�X���擾����
 * �����@�Fnone
 * �߂�l�Fpath
 * �쐬�@�F2014/09/11
 */	

	public static String getExternalStoragePath(){
		String path = "";
		
		//MOTOROLA�Ή�
		path = System.getenv("EXTERNAL_ALT_STORAGE");
		
		//Sumsung�Ή�
		path = System.getenv("EXTERNAL_STRAGE2");
		if(path != null)
			return path;
		
		//��Sumsung + �W���Ή�
		path = System.getenv("EXTERNAL_STRAGE");
		if(path == null)
			path = Environment.getExternalStorageDirectory().getPath();
		
		//HTC�Ή�
		File file = new File(path + "/ext_sd");
		if(file.exists())
			path = file.getPath();
		
		//���̑��@��Ή�
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
