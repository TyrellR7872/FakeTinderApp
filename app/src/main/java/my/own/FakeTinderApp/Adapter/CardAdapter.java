package my.own.FakeTinderApp.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.huxq17.swipecardsview.BaseCardAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import my.own.FakeTinderApp.Model.Model;
import my.own.FakeTinderApp.R;

public class CardAdapter extends BaseCardAdapter{

    private List<Model> modelList;
    private Context context;
    private String gender;

    public CardAdapter(List<Model> modelList, String gender, Context context) {
        this.modelList = modelList;
        this.context = context;
        this.gender = gender;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.card_item;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if (modelList == null || modelList.size() == 0){
            return;
        }
        ImageView imageView = cardview.findViewById(R.id.imageView);

        Model model = modelList.get(position);

        if(gender.equals("male")){
            Picasso.with(context).load("file:///android_asset/malePics/"+model.getImage()).into(imageView);
        } else{
            Picasso.with(context).load("file:///android_asset/femalePics/"+model.getImage()).into(imageView);

        }
    }
}
