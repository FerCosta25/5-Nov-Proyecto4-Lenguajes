package com.example.applenguajes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class listado extends AppCompatActivity {

    private final String[] cabecera = {"Nombre", "Apellido", "Cédula"};
    private final ArrayList<String> datos = new ArrayList<>();
    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        dbHelper = new FeedReaderDbHelper(this);
        TableLayout tblistado = findViewById(R.id.tblistado);
        TextView txtvacio = findViewById(R.id.txtvacio);
        TraerDatos();
        if (datos.isEmpty()){
            txtvacio.setVisibility(TextView.VISIBLE);
        } else {
            DynamicTable creaTabla = new DynamicTable(tblistado, this);
            creaTabla.setCabecera(cabecera);
            creaTabla.setDatos((ArrayList<String[]>) datos);
            creaTabla.crearCabecera();
            creaTabla.crearFilas();
        }
    }
    @Override
    protected void onDestroy(){
        dbHelper.close();
        super.onDestroy();
    }
    private void TraerDatos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2
        };
        String sortOrder = FeedReaderContract.FeedEntry.column2 + "ASC";
        try(Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        )) {
            while (cursor.moveToNext()) {
                String[] fila = new String[3];
                fila[0] = String.valueOf(cursor.getLong(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID)));
                fila[1] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
                fila[2] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
                datos.add(Arrays.toString(fila));
            }
        }
    }
    public void Regresar(View vista) {finish();}
}