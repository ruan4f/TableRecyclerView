package com.app.feng.tablerecyclerview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.app.feng.fixtablelayout.FixTableLayout;
import com.app.feng.fixtablelayout.inter.ILoadMoreListener;
import com.app.feng.tablerecyclerview.bean.DataBean;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainSelectableActivity extends AppCompatActivity {

    public String[] title = {"Id", "title1", "title grande para o texto", "title3", "title4", "title5", "title6", "title7",
            "titulo mais grande de texto para teste"};

    public List<DataBean> data = new ArrayList<>();

    int currentPage = 1;
    int totalPage = 5;

    private FixTableLayout fixTableLayout;

    private DataBean itemSelected;

    private Button btnReenviar;
    private Button btnExcluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_selectable);

        for (int i = 0; i < 20; i++) {
            this.data.add(new DataBean("id__", "Eu estou testando texto grande", "data" + i, "data3", "Texto grande será que funciona", "data5", "data6", "data7",
                    "data8"));
        }

        this.btnReenviar = findViewById(R.id.btnReenviar);
        this.btnExcluir = findViewById(R.id.btnExcluir);
        this.fixTableLayout = findViewById(R.id.fixTableLayout);

        final FixTableAdapter fixTableAdapter = new FixTableAdapter(title, data, MainSelectableActivity.this);

        this.fixTableLayout.setAdapter(fixTableAdapter);

        initListeners();
    }

    private void initListeners() {
        this.btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = fixTableLayout.getPositionSelected();

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainSelectableActivity.this);

                if (index > -1) {
                    itemSelected = data.get(fixTableLayout.getPositionSelected());

                    builder
                            .setMessage("Deseja excluir o livro " + itemSelected.data2 + "?")
                            .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .show();
                } else {
                    builder
                            .setMessage("Por favor selecione um livro para excluir")
                            .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        this.btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = fixTableLayout.getPositionSelected();

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainSelectableActivity.this);

                if (index > -1) {
                    itemSelected = data.get(fixTableLayout.getPositionSelected());

                    builder
                            .setMessage("Deseja reenviar o livro " + itemSelected.data2 + "?")
                            .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .show();
                } else {
                    builder
                            .setMessage("Por favor selecione um livro para reenviar")
                            .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }


}
