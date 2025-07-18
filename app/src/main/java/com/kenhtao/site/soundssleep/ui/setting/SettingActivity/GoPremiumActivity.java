package com.kenhtao.site.soundssleep.ui.setting.SettingActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.kenhtao.site.soundssleep.R;

import java.util.List;

public class GoPremiumActivity extends AppCompatActivity {
    private LinearLayout planYearly, planLifetime;
    private TextView tvYearlyPrice, tvLifetimePrice;

    private BillingClient billingClient;
    private List<ProductDetails> productDetailsList;

    // Product IDs từ Google Play Console
    private static final String YEARLY_ID = "premium_yearly";
    private static final String LIFETIME_ID = "premium_lifetime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);

        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        initViews();
        setupBillingClient();
    }

    private void initViews() {
        // Gói Yearly
        planYearly = findViewById(R.id.planYearly);
        tvYearlyPrice = planYearly.findViewById(R.id.tvPlanPrice);

        // Gói Lifetime
        planLifetime = findViewById(R.id.planLifetime);
        tvLifetimePrice = planLifetime.findViewById(R.id.tvPlanPrice);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    }
                })
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(GoPremiumActivity.this, "Mất kết nối tới Google Play", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                } else {
                    Toast.makeText(GoPremiumActivity.this, "Billing setup failed: " + billingResult.getDebugMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = List.of(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(YEARLY_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(LIFETIME_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, detailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                productDetailsList = detailsList;
                updateUI(detailsList);
            } else {
                Toast.makeText(this, "Không thể lấy giá từ Google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<ProductDetails> detailsList) {
        for (ProductDetails product : detailsList) {
            String productId = product.getProductId();
            String formattedPrice = "";

            if (product.getOneTimePurchaseOfferDetails() != null) {
                formattedPrice = product.getOneTimePurchaseOfferDetails().getFormattedPrice();
            } else if (!product.getSubscriptionOfferDetails().isEmpty()) {
                formattedPrice = product.getSubscriptionOfferDetails()
                        .get(0).getPricingPhases()
                        .getPricingPhaseList().get(0).getFormattedPrice();
            }

            switch (productId) {
                case YEARLY_ID:
                    tvYearlyPrice.setText(formattedPrice);
                    planYearly.setOnClickListener(v -> launchBillingFlow(product));
                    break;
                case LIFETIME_ID:
                    tvLifetimePrice.setText(formattedPrice);
                    planLifetime.setOnClickListener(v -> launchBillingFlow(product));
                    break;
            }
        }
    }

    private void launchBillingFlow(ProductDetails productDetails) {
        BillingFlowParams billingFlowParams;

        if (productDetails.getOneTimePurchaseOfferDetails() != null) {
            billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(List.of(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .build()
                    ))
                    .build();
        } else {
            String offerToken = productDetails.getSubscriptionOfferDetails().get(0).getOfferToken();
            billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(List.of(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build()
                    ))
                    .build();
        }

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void handlePurchase(Purchase purchase) {
        Toast.makeText(this, "🎉 Mua thành công!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) billingClient.endConnection();
    }
}
