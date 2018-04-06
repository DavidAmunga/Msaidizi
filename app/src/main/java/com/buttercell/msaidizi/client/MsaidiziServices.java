package com.buttercell.msaidizi.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.buttercell.msaidizi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MsaidiziServices extends AppCompatActivity {

    @BindView(R.id.cardPlumber)
    CardView cardPlumber;
    @BindView(R.id.cardMechanic)
    CardView cardMechanic;
    @BindView(R.id.cardElectrician)
    CardView cardElectrician;
    @BindView(R.id.cardHouseHelp)
    CardView cardHouseHelp;
    @BindView(R.id.cardCarpenter)
    CardView cardCarpenter;
    @BindView(R.id.cardHandyMan)
    CardView cardHandyMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msaidizi_services);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.cardPlumber, R.id.cardMechanic, R.id.cardElectrician, R.id.cardHouseHelp, R.id.cardCarpenter, R.id.cardHandyMan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cardPlumber:
                startActivity(new Intent(MsaidiziServices.this,SearchMsaidizi.class).putExtra("skill","Plumber"));
                break;
            case R.id.cardMechanic:
                startActivity(new Intent(MsaidiziServices.this,SearchMsaidizi.class).putExtra("skill","Mechanic"));
                break;
            case R.id.cardElectrician:
                startActivity(new Intent(MsaidiziServices.this,SearchMsaidizi.class).putExtra("skill","Electrician"));
                break;
            case R.id.cardHouseHelp:
                startActivity(new Intent(MsaidiziServices.this,SearchMsaidizi.class).putExtra("skill","House help"));
                break;
            case R.id.cardCarpenter:
                startActivity(new Intent(MsaidiziServices.this,SearchMsaidizi.class).putExtra("skill","Carpenter"));
                break;
            case R.id.cardHandyMan:
                startActivity(new Intent(MsaidiziServices.this,SearchMsaidizi.class).putExtra("skill","Handy man"));
                break;
        }
    }
}
