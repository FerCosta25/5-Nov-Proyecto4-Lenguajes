package com.example.applenguajes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText txtid;
    private EditText txtnombre;
    private EditText txtapellido;
    private FeedReaderDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        txtid = findViewById(R.id.etCedula);
        txtnombre = findViewById(R.id.etNombre);
        txtapellido = findViewById(R.id.etApellido);
        dbHelper = new FeedReaderDbHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onDestroy(){
        dbHelper.close();
        super.onDestroy();
    }
    public void Listar(View vista) {
        startActivity(new Intent(this, Listado.class));
    }

    public void Guardar(View vista) {
        String nombre = txtnombre.getText().toString().trim();
        String apellido = txtapellido.getText().toString().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingrese nombre y apellido");
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        long newRowId = db.insert(FeedReaderContract.FeedEntry.nameTable, null, values);
        if (newRowId == -1) {
            mostrarMensaje("No se pudo guardar el registro");
        } else {
            txtid.setText(String.valueOf(newRowId));
            mostrarMensaje("Se guardó el registro con clave: " + newRowId);
        }
    }
    public void Buscar(View vista) {
        String id = leerId();
        if (id == null) return;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2};
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        try (Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
        )) {
            if (cursor.moveToFirst()) {
                txtnombre.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1)));
                txtapellido.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2)));
            } else {
                txtnombre.setText("");
                txtapellido.setText("");
                mostrarMensaje("No existe un registro con Id " + id);
            }
        }
    }

    public void Actualizar (View vista) {
        String id = leerId();
        if (id == null) return;
        String nombre = txtnombre.getText().toString().trim();
        String apellido = txtapellido.getText().toString().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingrese nombre y apellido");
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        int count = db.update(FeedReaderContract.FeedEntry.nameTable, values, selection, selectionArgs);
        mostrarMensaje("Se actualizó " + count + "registro(s_");
    }
}
