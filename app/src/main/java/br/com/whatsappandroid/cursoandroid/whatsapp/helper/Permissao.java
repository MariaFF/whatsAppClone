package br.com.whatsappandroid.cursoandroid.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria on 16/03/2018.
 */

public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes){

        if(Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<String>();

            for (String permissao : permissoes) {
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                //caso você não tenha a permissão ela será guardada na listaPermissoes
                if (!validaPermissao)
                    listaPermissoes.add(permissao);
            }
            //pegando a lista de permissoes e transformando em array de Strings
            String [] novasPermissoes = new String [listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Lista vazia, a permissão já foi solicitada
            if(listaPermissoes.isEmpty()){
                return true;
            }

            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);

        }

        return true;
    }
}
