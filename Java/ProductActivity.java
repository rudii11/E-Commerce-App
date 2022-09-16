package internship.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ProductList> arrayList;

    String[] productNameArray = {"Apple", "Banana", "Blue Berry", "Grape", "Strawberry"};
    int[] productImageArray = {R.drawable.apple_banne2, R.drawable.banana1, R.drawable.blue_blueberries, R.drawable.grapes, R.drawable.strawberry};
    String[] productPriceArray = {"200", "100", "300", "100", "40"};
    String[] productUnitArray = {"1 KG", "12 Items", "500 GM", "500 GM", "1 Box"};
    String[] productDescriptionArray = {
            "An apple is an edible fruit produced by an apple tree. Apple trees are cultivated worldwide and are the most widely grown species in the genus Malus. The tree originated in Central Asia, where its wild ancestor, Malus sieversii, is still found today.",
            "The fruit is variable in size, color, and firmness, but is usually elongated and curved, with soft flesh rich in starch covered with a rind, which may be green, yellow, red, purple, or brown when ripe. The fruits grow upward in clusters near the top of the plant.",
            "The fruit is variable in size, color, and firmness, but is usually elongated and curved, with soft flesh rich in starch covered with a rind, which may be green, yellow, red, purple, or brown when ripe. The fruits grow upward in clusters near the top of the plant.",
            "Grapes are a type of fruit that grow in clusters of 15 to 300, and can be crimson, black, dark blue, yellow, green, orange, and pink. \"White\" grapes are actually green in color, and are evolutionarily derived from the purple grape.",
            "A strawberry is a small red fruit which is soft and juicy and has tiny yellow seeds on its skin."
    };

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle("Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        recyclerView = findViewById(R.id.product_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*arrayList = new ArrayList<>();
        for (int i = 0; i < productNameArray.length; i++) {
            ProductList list = new ProductList();
            list.setName(productNameArray[i]);
            list.setPrice(productPriceArray[i]);
            list.setUnit(productUnitArray[i]);
            list.setDescription(productDescriptionArray[i]);
            list.setImage(productImageArray[i]);
            arrayList.add(list);
        }
        ProductListAdapter adapter = new ProductListAdapter(ProductActivity.this, arrayList);
        recyclerView.setAdapter(adapter);*/
        if (new ConnectionDetector(ProductActivity.this).isConnectingToInternet()) {
            new getProductData().execute();
        } else {
            new ConnectionDetector(ProductActivity.this).connectiondetect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class getProductData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ProductActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (sp.getString(ConstantSp.CATEGORY_ID, "").equalsIgnoreCase("")) {
                return new MakeServiceCall().MakeServiceCall(ConstantUrl.PRODUCT_ALL_URL, MakeServiceCall.GET, new HashMap<>());
            }
            else{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("categoryId",sp.getString(ConstantSp.CATEGORY_ID,""));
                return new MakeServiceCall().MakeServiceCall(ConstantUrl.PRODUCT_URL, MakeServiceCall.POST, hashMap);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("Status") == true) {
                    JSONArray jsonArray = object.getJSONArray("ProductData");
                    arrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ProductList list = new ProductList();
                        list.setName(jsonObject.getString("name"));
                        list.setPrice(jsonObject.getString("price"));
                        list.setUnit(jsonObject.getString("unit"));
                        list.setDescription(jsonObject.getString("description"));
                        list.setImage(jsonObject.getString("image"));
                        arrayList.add(list);
                    }
                    ProductListAdapter productAdapter = new ProductListAdapter(ProductActivity.this, arrayList);
                    recyclerView.setAdapter(productAdapter);
                } else {
                    new CommonMethod(ProductActivity.this, object.getString("Message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new CommonMethod(ProductActivity.this, e.getMessage());
            }
        }
    }

}