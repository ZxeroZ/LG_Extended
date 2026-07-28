    private void ocultarTextoPorcentaje(XC_MethodHook.MethodHookParam param) {
        try {
            TextView textoPorcentaje = (TextView) XposedHelpers.getObjectField(param.thisObject, "mBatteryLevel");
            if (textoPorcentaje != null) {
                textoPorcentaje.setVisibility(View.GONE);
                // Remove from parent so it NEVER shows up again
                if (textoPorcentaje.getParent() instanceof ViewGroup) {
                    ((ViewGroup) textoPorcentaje.getParent()).removeView(textoPorcentaje);
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("LG_Extended/Battery: error ocultando texto de porcentaje: " + t.getMessage());
        }
    }
