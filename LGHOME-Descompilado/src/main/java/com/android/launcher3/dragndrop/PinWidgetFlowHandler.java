package com.android.launcher3.dragndrop;

import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.Launcher;
import com.android.launcher3.compat.PinItemRequestCompat;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.widget.WidgetAddFlowHandler;

/* JADX INFO: loaded from: classes.dex */
public class PinWidgetFlowHandler extends WidgetAddFlowHandler implements Parcelable {
    public static final Parcelable.Creator<PinWidgetFlowHandler> CREATOR = new Parcelable.Creator<PinWidgetFlowHandler>() { // from class: com.android.launcher3.dragndrop.PinWidgetFlowHandler.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinWidgetFlowHandler createFromParcel(Parcel source) {
            return new PinWidgetFlowHandler(source);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinWidgetFlowHandler[] newArray(int size) {
            return new PinWidgetFlowHandler[size];
        }
    };
    private final PinItemRequestCompat mRequest;

    @Override // com.android.launcher3.widget.WidgetAddFlowHandler
    public boolean needsConfigure() {
        return false;
    }

    public PinWidgetFlowHandler(AppWidgetProviderInfo providerInfo, PinItemRequestCompat request) {
        super(providerInfo);
        this.mRequest = request;
    }

    protected PinWidgetFlowHandler(Parcel parcel) {
        super(parcel);
        this.mRequest = PinItemRequestCompat.CREATOR.createFromParcel(parcel);
    }

    @Override // com.android.launcher3.widget.WidgetAddFlowHandler, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        this.mRequest.writeToParcel(parcel, i);
    }

    @Override // com.android.launcher3.widget.WidgetAddFlowHandler
    public boolean startConfigActivity(Launcher launcher, int appWidgetId, ItemInfo info, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt("appWidgetId", appWidgetId);
        this.mRequest.accept(bundle);
        return false;
    }
}
