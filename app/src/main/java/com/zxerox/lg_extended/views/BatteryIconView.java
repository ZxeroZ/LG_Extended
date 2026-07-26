package com.zxerox.lg_extended.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class BatteryIconView extends View {

    public enum Estilo {
        IOS_26,
        ONEUI_9,
        ONEUI_8,
        IOS_17
    }

    private int nivel = 100;
    private boolean cargando = false;
    private Estilo estilo = Estilo.ONEUI_8;

    private Paint fondoPaint;
    private Paint textoPaint;
    private RectF fondoRect;

    private int anchoDeseado;
    private int altoDeseado;
    private float densidad;

    // Colores de Fondo
    private int colorFondo = Color.parseColor("#1C1C1E");
    private int colorFondoCargando = Color.parseColor("#34C759");
    private int colorFondoBajo = Color.parseColor("#FF3B30");

    // Colores de Texto
    private int colorTexto = Color.WHITE;
    private int colorTextoCargando = Color.WHITE;
    private int colorTextoBajo = Color.WHITE;

    public BatteryIconView(Context context) {
        super(context);
        init(context);
    }

    public BatteryIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        densidad = context.getResources().getDisplayMetrics().density;
        anchoDeseado = (int) (30 * densidad);
        altoDeseado = (int) (16 * densidad);

        fondoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fondoPaint.setStyle(Paint.Style.FILL);

        textoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textoPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        textoPaint.setTextAlign(Paint.Align.CENTER);
        textoPaint.setFakeBoldText(true);

        fondoRect = new RectF();
    }

    // --- SETTERS DE COLORES PARA EL COLOR PICKER ---

    public void setColoresNormal(int colorFondo, int colorTexto) {
        this.colorFondo = colorFondo;
        this.colorTexto = colorTexto;
        postInvalidate();
    }

    public void setColoresCargando(int colorFondo, int colorTexto) {
        this.colorFondoCargando = colorFondo;
        this.colorTextoCargando = colorTexto;
        postInvalidate();
    }

    public void setColoresBateriaBaja(int colorFondo, int colorTexto) {
        this.colorFondoBajo = colorFondo;
        this.colorTextoBajo = colorTexto;
        postInvalidate();
    }

    // -----------------------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int extra;
        if (estilo == Estilo.IOS_26) {
            extra = (int) (2 * densidad);
        } else if (estilo == Estilo.ONEUI_9) {
            float nubDiametro = altoDeseado * 0.42f;
            float solapamiento = (nubDiametro / 2f) * 0.5f;
            extra = (int) (nubDiametro - solapamiento);
        } else {
            extra = 0;
        }
        setMeasuredDimension(anchoDeseado + extra, altoDeseado);
    }

    public void actualizarEstado(int nuevoNivel, boolean estaCargando) {
        this.nivel = nuevoNivel;
        this.cargando = estaCargando;
        postInvalidate();
    }

    public void setEstilo(Estilo nuevoEstilo) {
        this.estilo = nuevoEstilo;
        requestLayout();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int ancho = getWidth();
        int alto = getHeight();
        if (ancho == 0 || alto == 0) return;

        switch (estilo) {
            case IOS_26:
                dibujarIos26(canvas, ancho, alto);
                break;
            case ONEUI_9:
                dibujarOneUi9(canvas, ancho, alto);
                break;
            case IOS_17:
                dibujarIos17(canvas, ancho, alto);
                break;
            case ONEUI_8:
            default:
                dibujarOneUi8(canvas, ancho, alto);
                break;
        }
    }

    // --- LÓGICA DE DECISIÓN DE COLORES ---

    private int colorFondoActual() {
        if (cargando) return colorFondoCargando;
        if (nivel <= 20) return colorFondoBajo;
        return colorFondo;
    }

    private int colorTextoActual() {
        if (cargando) return colorTextoCargando;
        if (nivel <= 20) return colorTextoBajo;
        return colorTexto;
    }

    // --- MÉTODOS DE DIBUJO ---

    private void dibujarIos26(Canvas canvas, int ancho, int alto) {
        float nubWidth = 3f * densidad;
        float cuerpoAncho = ancho - nubWidth;
        float radio = alto * 0.32f;

        fondoPaint.setColor(colorFondoActual());

        fondoRect.set(0, 0, cuerpoAncho, alto);
        canvas.drawRoundRect(fondoRect, radio, radio, fondoPaint);

        float nubAlto = alto * 0.38f;
        float nubAncho = nubWidth * 0.7f;
        float nubTop = (alto - nubAlto) / 2f;
        float nubLeft = cuerpoAncho + (nubWidth - nubAncho) / 2f;
        RectF nubRect = new RectF(nubLeft, nubTop, nubLeft + nubAncho, nubTop + nubAlto);
        canvas.drawRoundRect(nubRect, nubAncho / 2f, nubAncho / 2f, fondoPaint);

        textoPaint.setColor(colorTextoActual());
        dibujarTexto(canvas, cuerpoAncho / 2f, alto, alto * 0.72f);
    }

    private void dibujarOneUi9(Canvas canvas, int ancho, int alto) {
        float nubDiametro = alto * 0.42f;
        float nubRadio = nubDiametro / 2f;
        float radio = alto / 2f;

        fondoPaint.setColor(colorFondoActual());

        float solapamiento = nubRadio * 0.5f;
        float cuerpoAncho = ancho - (nubDiametro - solapamiento);
        fondoRect.set(0, 0, cuerpoAncho, alto);
        canvas.drawRoundRect(fondoRect, radio, radio, fondoPaint);

        float nubCenterX = cuerpoAncho - solapamiento;
        float nubCenterY = alto / 2f;
        canvas.drawCircle(nubCenterX, nubCenterY, nubRadio, fondoPaint);

        textoPaint.setColor(colorTextoActual());
        float textoCentro = cuerpoAncho / 2f;
        dibujarTexto(canvas, textoCentro, alto, alto * 0.85f);
    }

    private void dibujarIos17(Canvas canvas, int ancho, int alto) {
        float nubWidth = 3f * densidad;
        float cuerpoAncho = ancho - nubWidth;
        float radioExterior = alto * 0.26f;

        float grosorBorde = 3.0f * densidad;
        float paddingInterior = 1.5f * densidad;

        fondoPaint.setStyle(Paint.Style.STROKE);
        fondoPaint.setStrokeWidth(grosorBorde);
        fondoPaint.setColor(colorTextoActual());

        float inset = grosorBorde / 2f;
        fondoRect.set(inset, inset, cuerpoAncho - inset, alto - inset);
        canvas.drawRoundRect(fondoRect, radioExterior, radioExterior, fondoPaint);

        fondoPaint.setStyle(Paint.Style.FILL);
        float nubAlto = alto * 0.40f;
        float nubAncho = nubWidth * 0.8f;
        float nubTop = (alto - nubAlto) / 2f;
        float nubLeft = cuerpoAncho + (nubWidth - nubAncho) / 2f;
        RectF nubRect = new RectF(nubLeft, nubTop, nubLeft + nubAncho, nubTop + nubAlto);
        canvas.drawRoundRect(nubRect, nubAncho / 2f, nubAncho / 2f, fondoPaint);

        float paddingTotal = grosorBorde + paddingInterior;
        float anchoMaximoRelleno = cuerpoAncho - (paddingTotal * 2f);
        float anchoRellenoActual = anchoMaximoRelleno * (nivel / 100f);
        float radioInterior = radioExterior - paddingInterior;

        if (anchoRellenoActual < radioInterior * 2f && nivel > 0) {
            anchoRellenoActual = radioInterior * 2f;
        }

        fondoPaint.setColor(colorFondoActual());

        if (nivel > 0) {
            RectF rellenoRect = new RectF(paddingTotal, paddingTotal, paddingTotal + anchoRellenoActual, alto - paddingTotal);
            canvas.drawRoundRect(rellenoRect, radioInterior, radioInterior, fondoPaint);
        }

        textoPaint.setColor(colorTextoActual());
        dibujarTexto(canvas, cuerpoAncho / 2f, alto, alto * 0.65f);
    }
    private void dibujarOneUi8(Canvas canvas, int ancho, int alto) {
        float radio = alto / 2f;

        fondoPaint.setColor(colorFondoActual());
        fondoRect.set(0, 0, ancho, alto);
        canvas.drawRoundRect(fondoRect, radio, radio, fondoPaint);

        textoPaint.setColor(colorTextoActual());
        dibujarTexto(canvas, ancho / 2f, alto, alto * 0.85f);
    }

    private void dibujarTexto(Canvas canvas, float xCentro, int alto, float tamanoTexto) {
        textoPaint.setTextSize(tamanoTexto);
        String texto = String.valueOf(nivel);
        float yCentro = (alto / 2f) - ((textoPaint.descent() + textoPaint.ascent()) / 2f);
        canvas.drawText(texto, xCentro, yCentro, textoPaint);
    }
}