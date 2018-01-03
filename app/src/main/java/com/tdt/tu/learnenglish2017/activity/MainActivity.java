package com.tdt.tu.learnenglish2017.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.Tab1Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab2Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab3Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab4Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab5Fragment;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    SectionsPagerAdapter sectionsPagerAdapter;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.bottomNavigation)
    AHBottomNavigation bottomNavigation;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        authentication();
        isPermissionGranted();
        setupViewPager(viewPager);
        addBottomNavigationItems();
        setupBottomNavigationStyle();
        navigationTabHandler();
        createAppFolder();
    }

    private void askFirstQuiz() {
        boolean isFirstRun = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getBoolean("first_run", true);
        editor = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();

        if (isFirstRun) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Hold on...");
            builder.setMessage("Would you like to take a proficiency test ?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(MainActivity.this, FirstQuizActivity.class));
                }
            });
            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();

            editor.putBoolean("first_run", false).commit();
        }
    }

    private void authentication() {
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void navigationTabHandler() {
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    viewPager.setCurrentItem(position);
                }

                return true;
            }
        });
    }


    private void init() {
        ButterKnife.bind(this);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        bottomNavigation.setCurrentItem(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        sectionsPagerAdapter.addFragment(new Tab1Fragment());
        sectionsPagerAdapter.addFragment(new Tab2Fragment());
        sectionsPagerAdapter.addFragment(new Tab3Fragment());
        sectionsPagerAdapter.addFragment(new Tab4Fragment());
        sectionsPagerAdapter.addFragment(new Tab5Fragment());

        viewPager.setAdapter(sectionsPagerAdapter);
    }

    private void setupBottomNavigationStyle() {
        bottomNavigation.setColoredModeColors(fetchColor(R.color.colorWhite), fetchColor(R.color.colorSpaceGrey));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setColored(true);
    }

    private void addBottomNavigationItems() {
        AHBottomNavigationItem featuredTab = new AHBottomNavigationItem(R.string.featured, R.drawable.ic_featured, R.color.colorOrange);
        AHBottomNavigationItem searchTab = new AHBottomNavigationItem(R.string.search, R.drawable.ic_search, R.color.colorOrange);
        AHBottomNavigationItem mineTab = new AHBottomNavigationItem(R.string.mine, R.drawable.ic_mine, R.color.colorOrange);
        AHBottomNavigationItem favoriteTab = new AHBottomNavigationItem(R.string.favorite, R.drawable.ic_favorite, R.color.colorOrange);
        AHBottomNavigationItem summaryTab = new AHBottomNavigationItem(R.string.summary, R.drawable.ic_summary, R.color.colorOrange);

        bottomNavigation.addItem(featuredTab);
        bottomNavigation.addItem(searchTab);
        bottomNavigation.addItem(mineTab);
        bottomNavigation.addItem(favoriteTab);
        bottomNavigation.addItem(summaryTab);

    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private void createAppFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LearnEnglish2017/Download";
        File appFolder = new File(path);
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createAppFolder();
            askFirstQuiz();
        }
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission required")
                        .setMessage("You must allow this app to access files on your device to download lessons!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        } else {
            return true;
        }
    }
}
