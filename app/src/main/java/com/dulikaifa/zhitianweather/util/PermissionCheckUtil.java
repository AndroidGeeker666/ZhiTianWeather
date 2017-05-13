package com.dulikaifa.zhitianweather.util;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;

import com.dulikaifa.zhitianweather.R;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;


/**
 * Created by hasee on 2017/5/12.
 */

public class PermissionCheckUtil {

    public static int checkRuntimePermissin(final Context context, String permissionName, int customPermissionNameRes, int cutomDrawableRes ){
        final int[] result = new int[1];
        List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
        permissonItems.add(new PermissonItem(permissionName, context.getString(customPermissionNameRes),cutomDrawableRes));
        HiPermission.create(context)
                .title(context.getString(R.string.permission_cus_title))
                .permissions(permissonItems)
                .filterColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, context.getTheme()))
                .msg(context.getString(R.string.permission_cus_msg))
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {


                        result[0] =1;
                    }

                    @Override
                    public void onFinish() {

                        result[0] =2;
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        result[0] =3;

                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                        result[0] =4;
                    }
                });
        return result[0];
    }

}
