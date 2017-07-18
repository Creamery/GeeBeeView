package seebee.geebeeview.model.monitoring;

import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


/**
 * Created by Joy on 7/18/2017.
 */

public class LineChartValueFormatter  {
    private final static float[] visualAcuity = {-200f, -100f, -70f, -50f, -40f, -30f, -30f, -25f, 20f, -15f, -10f, -5f};
    private final static String TAG = "LineChartValueFormatter";

    public static float ConvertVisualAcuity(String visualAcuity) {
        float value = 0;
        switch (visualAcuity) {
            case "20/200": value = -200f;
                break;
            case "20/100": value = -100f;
                break;
            case "20/70": value = -70f;
                break;
            case "20/50": value = -50f;
                break;
            case "20/40": value = -40f;
                break;
            case "20/30": value = -30f;
                break;
            case "20/25": value = -25f;
                break;
            default:
            case "20/20": value = -20f;
                break;
            case "20/15": value = -15f;
                break;
            case "20/10": value = -10f;
                break;
            case "20/5": value = -5f;
                break;
        }
        return value;
    }

    public static YAxisValueFormatter getYAxisValueFormatterVisualAcuity() {
        Log.v(TAG, "getYAxisValueFormatter");
        return new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return ConvertVisualAcuity(v);
            }
        };
    }

    private static String ConvertVisualAcuity(float v) {
        String result;
        if(v <= -200f) {
            result = "20/200";
        } else if(v <= -100f) {
            result = "20/100";
        } else if(v <= -70f) {
            result = "20/70";
        } else if(v <= -50f) {
            result = "20/50";
        } else if(v <= -40f) {
            result = "20/40";
        } else if(v <= -30f) {
            result = "20/30";
        } else if(v <= -25f) {
            result = "20/25";
        } else if(v <= -20f) {
            result = "20/20";
        } else if(v <= -15f) {
            result = "20/15";
        } else if(v <= -10f) {
            result = "20/10";
        } else if(v <= -5f) {
            result = "20/5";
        } else {
            result = "N/A";
        }
        return result;
    }

    public static ValueFormatter getValueFormatterVisualAcuity() {
        return new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertVisualAcuity(v);
            }
        };
    }

    public static float ConvertColorVision(String colorVision) {
        float result;
        if(colorVision.contentEquals("Abnormal")) {
            result = 0;
        } else {
            result = 1;
        }
        return result;
    }

    public static YAxisValueFormatter getYAxisValueFormatterColorVision() {
        return new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return ConvertColorVision(v);
            }
        };
    }

    private static String ConvertColorVision(float v) {
        String result;
        if(v == 1) {
            result = "Normal";
        } else {
            result = "Abnormal";
        }
        return result;
    }

    public static ValueFormatter getValueFormatterColorVision() {
        return new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertColorVision(v);
            }
        };
    }

    public static float ConvertHearing(String hearing) {
        float result = 0;
        switch (hearing) {
            case "Normal Hearing": result = 1;
                break;
            case "Mild Hearing Loss": result = -1;
                break;
            case "Moderate Hearing Loss": result = -2;
                break;
            case "Moderately-Servere Hearing Loss": result = -3;
                break;
            case "Severe Hearing Loss": result = -4;
                break;
            case "Profound Hearing Loss": result = -5;
                break;
        }
        return result;
    }

    private static String ConvertHearing(float value) {
        String result = "N/A";
        if(value >= 1) {
            result = "Normal Hearing";
        } else if(value >= -1) {
            result = "Mild Hearing Loss";
        } else if(value >= -2) {
            result = "Moderate Hearing Loss";
        } else if(value >= -3) {
            result = "Moderately-Servere Hearing Loss";
        } else if(value >= -4) {
            result = "Severe Hearing Loss";
        } else if(value >= -5){
            result = "Profound Hearing Loss";
        }
        return result;
    }

    public static YAxisValueFormatter getYAxisValueFormatterHearing() {
        return new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return ConvertHearing(v);
            }
        };
    }

    public static ValueFormatter getValueFormatterHearing() {
        return new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertHearing(v);
            }
        };
    }

    private static String ConvertMotor(float v) {
        String result = "N/A";
        if(v == 0) {
            result = "Pass";
        } else if (v == 1) {
            result = "Fail";
        }
        return result;
    }

    public static YAxisValueFormatter getYAxisValueFormatterMotor() {
        return new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return ConvertMotor(v);
            }
        };
    }

    public static ValueFormatter getValueFormatterMotor() {
        return new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return ConvertMotor(v);
            }
        };
    }

    public static YAxisValueFormatter getYAxisValueFormatterBMI(final boolean isGirl, final int age) {
        return new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return BMICalculator.getBMIResultString(isGirl, age, v);
            }
        };
    }

    public static ValueFormatter getValueFormatterBMI(final boolean isGirl, final int age) {
        return new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return BMICalculator.getBMIResultString(isGirl, age, v);
            }
        };
    }
}
