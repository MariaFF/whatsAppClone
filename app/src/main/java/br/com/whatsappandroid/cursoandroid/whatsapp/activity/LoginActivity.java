package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.Random;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Permissao;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;

public class LoginActivity extends AppCompatActivity {
    private EditText campoNome;
    private EditText campoTelefone;
    private EditText campoCodPais;
    private EditText campoCodArea;
    private Button btCadastrar;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1, this, permissoesNecessarias);

        campoNome = (EditText) findViewById(R.id.login_nome);
        campoTelefone = (EditText) findViewById(R.id.login_telefone);
        campoCodPais = (EditText) findViewById(R.id.login_cod_pais);
        campoCodArea = (EditText) findViewById(R.id.login_cod_area);
        btCadastrar = (Button) findViewById(R.id.login_bt_cadastrar);

        //Usando a mascara
        SimpleMaskFormatter smCodPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter smCodArea = new SimpleMaskFormatter("(NN)");
        SimpleMaskFormatter smTelefone = new SimpleMaskFormatter("NNNNN-NNNN");


        //primeiro parametro o campo que vai receber a mascara
        // e o segundo é a mascara
        final MaskTextWatcher maskTelefone = new MaskTextWatcher(campoTelefone, smTelefone);
        MaskTextWatcher maskCodPais = new MaskTextWatcher(campoCodPais, smCodPais);
        MaskTextWatcher maskCodArea = new MaskTextWatcher(campoCodArea, smCodArea);

        campoCodPais.addTextChangedListener(maskCodPais);
        campoCodArea.addTextChangedListener(maskCodArea);
        campoTelefone.addTextChangedListener(maskTelefone);

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recuperar os dados

                String nomeUsuario = campoNome.getText().toString();
                String telefoneCompleto =
                        campoCodPais.getText().toString() +
                        campoCodArea.getText().toString() +
                        campoTelefone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("(", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace(")", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-", "");

                Log.i("Telefone", "Tel" + telefoneSemFormatacao);

                //GERAR TOKEN com 4 digitos aleatórios
                Random randomico = new Random();
                int numRandomico = randomico.nextInt( (9999 - 1000) + 1000);
                String token = String.valueOf(numRandomico);

                Log.i("TOKEN ", "NUM" +token);

                //Salvar os dados para validação
                Preferencias preferencias = new Preferencias(getApplicationContext());
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                String mensagemEnvio = "whatsApp Códio de Confirmação: " + token;

                //Envio do SMS
                telefoneSemFormatacao = "5554";
                boolean enviadoSMS = enviaSMS("+" + telefoneSemFormatacao, mensagemEnvio);


                //retornar um hashMap com os dados do usuario
                /*HashMap<String, String> usuario = preferencias.getDadosUsuario();
                Log.i("Token", "T" + usuario.get("token"));*/

            }
        });

    }

    /*Envio de SMS*/
    private boolean enviaSMS(String telefone, String mensagem){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);
            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //Caso o usuário nega a permissão
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        //chamando a implementação padrão do metodo e agora vamos usar as nossas verificações
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        for (int resultado : grantResult){
            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar esse App, é necessário aceitar as permissões");

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
