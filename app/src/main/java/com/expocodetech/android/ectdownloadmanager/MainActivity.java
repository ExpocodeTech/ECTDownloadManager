package com.expocodetech.android.ectdownloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.IllegalFormatCodePointException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String FILE_URL = "https://expocodetech.com/apps/downloads/fichero.txt";
    private static final String FILE_NAME = "fichero.txt";

    private DownloadManagerReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isDownloadManagerAvailable(this)) {
            registerUnregisterBroadcastReceiver(false);
            downloadBigFile(FILE_URL, FILE_NAME);
        }
    }

    private void registerUnregisterBroadcastReceiver(boolean unregister) {
        if (!isDownloadManagerAvailable(this))
            return;
        if (unregister) {
            //Desregistramos el DownloadManagerReceiver
            if (mReceiver != null)
                unregisterReceiver(mReceiver);
        } else {
            //Creamos un IntentFilter para que nuestro DownloadManagerReceiver solo cuando
            // el usuario haya hecho click en la notificación que el DownloadManage ha desplegado
            // por nosotros
            IntentFilter intentFilter = new IntentFilter();
            //Agregamos la acción ACTION_DOWNLOAD_COMPLETE para que se nos notifique cuando haya
            // culminado al descarga del fichero
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            mReceiver = new DownloadManagerReceiver();
            //Registramos el DownloadManagerReceiver
            registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    protected void onStop() {
        registerUnregisterBroadcastReceiver(true);
        super.onStop();
    }

    public static boolean isDownloadManagerAvailable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    public void downloadBigFile(String url, String fileName){
        //Instanciamos una petición de descarga de fichero
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //Seteamos una descripción para la notificación
        request.setDescription(getString(R.string.notif_Description));
        //Seteamos un título para la notificación
        request.setTitle(getString(R.string.notif_title));
        //Permitimos que el fichero a descargar sea escaneado por el MediaScanner
        request.allowScanningByMediaScanner();
        //Indicamos en la petición cuando queremos ahcer visible la Notificación
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //Le decimos
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // Obtenemos el servicio DownloadManager y enviamos a la cola de descargar el fichero que queremosdescargar
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public class DownloadManagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Este metodo se disparara cuando el usuario haga click sobre la notifiación que el
            // DownloadManager despliega por nosotros despues de haber culminado la descarga del fichero
            Log.d(TAG, "DownloadManagerReceiver onReceive");
            if (intent.getAction().equalsIgnoreCase(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                Toast.makeText(MainActivity.this, getString(R.string.toast_downloadComplete), Toast.LENGTH_SHORT).show();
            }
        }
    }


}



