package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cloudconcept.dwc.R;
import model.FreeZonePayment;
import utilities.ActivitiesLauncher;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/6/2015.
 */
public class ViewStatementAdapter extends ClickableListAdapter {

    Context context;
    Activity act;

    public ViewStatementAdapter(Activity act, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.context = context;
        this.act = act;
    }

    @Override
    protected ViewHolder createHolder(View v) {
        ImageView imageStatusDebitCredit;
        TextView tvEmployeeName;
        TextView tvDate;
        TextView tvAmount;
        TextView tvBalance;
        TextView tvStatus;

        TextView tvEmployeeNameLabel;
        TextView tvDateLabel;
        TextView tvAmountLabel;
        TextView tvBalanceLabel;
        TextView tvStatusLabel;


        tvEmployeeName = (TextView) v.findViewById(R.id.tvEmployeeName);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvAmount = (TextView) v.findViewById(R.id.tvAmount);
        tvBalance = (TextView) v.findViewById(R.id.tvBalance);
        tvStatus = (TextView) v.findViewById(R.id.tvStatus);
        imageStatusDebitCredit = (ImageView) v.findViewById(R.id.imageStatusDebitCredit);

        tvEmployeeNameLabel = (TextView) v.findViewById(R.id.tvEmployeeNameLabel);
        tvDateLabel = (TextView) v.findViewById(R.id.tvDateLabel);
        tvAmountLabel = (TextView) v.findViewById(R.id.tvAmountLabel);
        tvBalanceLabel = (TextView) v.findViewById(R.id.tvBalanceLabel);
        tvStatusLabel = (TextView) v.findViewById(R.id.tvStatusLabel);

        ViewHolder holder = new ViewHolder(imageStatusDebitCredit, tvEmployeeName, tvDate, tvAmount, tvBalance, tvStatus, tvEmployeeNameLabel, tvDateLabel, tvAmountLabel, tvBalanceLabel, tvStatusLabel);
        return holder;
    }

    @Override
    protected void bindHolder(ClickableListAdapter.ViewHolder h) {
        final ViewHolder mvh = (ViewHolder) h;
        FreeZonePayment mo = (FreeZonePayment) mvh.data;
        if (mo.getName() != null && !mo.getName().equals("")) {
            mvh.tvEmployeeName.setText(mo.getEmployeeName());
        } else {
            mvh.tvEmployeeName.setVisibility(View.GONE);
            mvh.tvEmployeeNameLabel.setVisibility(View.GONE);
        }

        mvh.tvStatus.setText(Utilities.stringNotNull(mo.getStatus()));

        if (mo.getEffectOnAccount().equals("Debit")) {
            mvh.tvAmount.setText(Utilities.stringNotNull(mo.getDebitAmount()) + " AED.");
            mvh.imageStatusDebitCredit.setBackgroundResource(R.mipmap.payment_debit);
            mvh.tvBalance.setVisibility(View.GONE);
            mvh.tvBalanceLabel.setVisibility(View.GONE);
        } else {
            mvh.tvAmount.setText(Utilities.stringNotNull(mo.getCreditAmount()));
            mvh.imageStatusDebitCredit.setBackgroundResource(R.mipmap.payment_credit);
            mvh.tvBalance.setText(Utilities.stringNotNull(mo.getClosingBalance()));
        }

        mvh.tvDate.setText(!Utilities.stringNotNull(mo.getCreatedDate()).equals("") ? Utilities.stringNotNull(mo.getCreatedDate()).substring(0, 10) : "");
        setupListeners(mvh);
    }

    private void setupListeners(final ViewHolder holder) {
        holder.tvEmployeeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
            }
        });
        holder.tvEmployeeNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvStatusLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvBalanceLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.tvAmountLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
        holder.imageStatusDebitCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesLauncher.openViewStatementShowDetails(context, (FreeZonePayment) holder.data);
                ;
            }
        });
    }

    public static class ViewHolder extends ClickableListAdapter.ViewHolder {
        private ImageView imageStatusDebitCredit;
        private TextView tvEmployeeName;
        private TextView tvDate;
        private TextView tvAmount;
        private TextView tvBalance;
        private TextView tvStatus;

        private TextView tvEmployeeNameLabel;
        private TextView tvDateLabel;
        private TextView tvAmountLabel;
        private TextView tvBalanceLabel;
        private TextView tvStatusLabel;

        public ViewHolder(ImageView imageStatusDebitCredit, TextView tvEmployeeName, TextView tvDate, TextView tvAmount, TextView tvBalance, TextView tvStatus, TextView tvEmployeeNameLabel, TextView tvDateLabel, TextView tvAmountLabel, TextView tvBalanceLabel, TextView tvStatusLabel) {
            this.imageStatusDebitCredit = imageStatusDebitCredit;
            this.tvEmployeeName = tvEmployeeName;
            this.tvDate = tvDate;
            this.tvAmount = tvAmount;
            this.tvBalance = tvBalance;
            this.tvStatus = tvStatus;
            this.tvEmployeeNameLabel = tvEmployeeNameLabel;
            this.tvDateLabel = tvDateLabel;
            this.tvAmountLabel = tvAmountLabel;
            this.tvBalanceLabel = tvBalanceLabel;
            this.tvStatusLabel = tvStatusLabel;
        }
    }
}
