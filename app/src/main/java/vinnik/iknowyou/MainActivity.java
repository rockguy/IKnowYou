package vinnik.iknowyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;
import support.IKYService;
import support.VKResponse;
import support.VKService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AccountFragment.OnFragmentInteractionListener
,HomeFragment.OnFragmentInteractionListener, WebViewFragment.OnFragmentInteractionListener {

    private static FragmentManager manager;
    private static FragmentTransaction transaction;
    private static VKService vkserv;
    private Retrofit retrofit;

    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.addToBackStack(null);

        final SharedPreferences prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        String id = prefs.getString("id", null);

        homeFragment = HomeFragment.newInstance(id);
        transaction.add(R.id.content_contaiter, homeFragment);
        transaction.commit();

        if (id == null || id.equals("")) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://mysterious-reaches-47552.herokuapp.com/") //сервер Антона
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            IKYService service = retrofit.create(IKYService.class);

            service.getId().enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", response.body().toString());
                    editor.commit();
                    homeFragment.updateQRCode(response.body().toString());
                    
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    System.out.println(t);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_account) {
            transaction = manager.beginTransaction();
            transaction.replace(R.id.content_contaiter, AccountFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.account_conteiner) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onCameraClick(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void NewAccountOnClick (View v) {
        transaction = manager.beginTransaction();
        switch (v.getId()){
            case R.id.vk_button:
                transaction.replace(R.id.content_contaiter, WebViewFragment.newInstance("https://oauth.vk.com/authorize?client_id=5490057&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.52"));
        }
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    scanResult.getContents(), Toast.LENGTH_SHORT);
            toast.show();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.vk.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            vkserv = retrofit.create(VKService.class);


            String accessToken = getSharedPreferences("settings", Context.MODE_PRIVATE).getString("vk_token","");

            MainActivity.getApi().addFriend(scanResult.getContents(), "5.63", accessToken).enqueue(new Callback<VKResponse>() {
                @Override
                public void onResponse(Call<VKResponse> call, Response<VKResponse> response) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            response.body().getResponseCode().toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }
                @Override
                public void onFailure(Call<VKResponse> call, Throwable t) {
                    System.out.println(t.toString());
                    Toast toast = Toast.makeText(getApplicationContext(),
                            t.toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }

        // else continue with any other code you need in the method
    }

    public static VKService getApi() {
        return vkserv;
    }
}
