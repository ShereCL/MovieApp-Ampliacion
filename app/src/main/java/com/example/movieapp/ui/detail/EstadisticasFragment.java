package com.example.movieapp.ui.detail;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieapp.R;
import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.databinding.FragmentEstadisticasBinding;
import com.example.movieapp.ui.viewModel.EstadisticasViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasFragment extends Fragment {

    private FragmentEstadisticasBinding binding;
    private EstadisticasViewModel viewModel;
    private String[] meses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Nombres de los meses para el gráfico
        meses = getResources().getStringArray(R.array.mesesAbreviados);
        viewModel = new ViewModelProvider(requireActivity()).get(EstadisticasViewModel.class);
        viewModel.getSeguimientos().observe(getViewLifecycleOwner(), lista -> {
            if (lista == null || lista.isEmpty()) {
                binding.tvPuntuacionMedia.setText(getString(R.string.noDisponible));
                binding.tvMesMasActivo.setText(getString(R.string.noDisponible));
                binding.tvGeneroMasVisto.setText(getString(R.string.sinDatosAun));
                binding.tvTotalPeliculas.setText(getString(R.string.cantidadPeliculas, 0));
                binding.tvTotalSeries.setText(getString(R.string.cantidadSeries, 0));
                return;
            }
            calcularYMostrar(lista);
        });
    }

    private void calcularYMostrar(List<SeguimientoFS> lista) {
        //totales por tipo
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String fechaHoy = sdf.format(java.util.Calendar.getInstance().getTime());

        int totalPelis = 0;
        int totalSeries = 0;

        for (SeguimientoFS s : lista) {
            String fecha = s.getFechaVisualizacion();
            if (fecha != null && fecha.compareTo(fechaHoy) <= 0) {
                String tipo = s.getTipo();
                if ("movie".equalsIgnoreCase(tipo) || "pelicula".equalsIgnoreCase(tipo)) {
                    totalPelis++;
                } else if ("tv".equalsIgnoreCase(tipo) || "serie".equalsIgnoreCase(tipo)) {
                    totalSeries++;
                }
            }
        }
        binding.tvTotalPeliculas.setText(getString(R.string.cantidadPeliculas, totalPelis));
        binding.tvTotalSeries.setText(getString(R.string.cantidadSeries, totalSeries));

        //puntuación media, solo entran las que tiene una puntuacion mayor a cero
        float sumaPuntuacion = 0;
        int contPuntuados = 0;
        for (SeguimientoFS s : lista) {
            if (s.getPuntuacion() > 0) {
                sumaPuntuacion += s.getPuntuacion();
                contPuntuados++;
            }
        }
        if (contPuntuados > 0) {
            float media = sumaPuntuacion / contPuntuados;
            binding.tvPuntuacionMedia.setText(String.format("%.1f / 10", media));
        } else {
            binding.tvPuntuacionMedia.setText(getString(R.string.sinPuntuaciones));
        }

        //Mes mas activo
        Map<String, Integer> conteoPorMes = new HashMap<>();
        for (SeguimientoFS s : lista) {
            String fecha = s.getFechaVisualizacion();
            if (fecha != null && fecha.length() >= 7) {
                String mesAnio = fecha.substring(0, 7); //equivale a yyyy-MM
                conteoPorMes.put(mesAnio, conteoPorMes.getOrDefault(mesAnio, 0) + 1);
            }
        }
        String mesMasActivo = "—";
        int maxMes = 0;
        for (Map.Entry<String, Integer> entry : conteoPorMes.entrySet()) {
            if (entry.getValue() > maxMes) {
                maxMes = entry.getValue();
                mesMasActivo = formatearMes(entry.getKey()); //esto equivale a yyyy-MM que equivale por ejemplo a Marzo 2024
            }
        }
        binding.tvMesMasActivo.setText(mesMasActivo);

        //genero mas visto
        String generoLabel = totalPelis >= totalSeries ? getString(R.string.peliculasEmoji) : getString(R.string.seriesEmoji);
        binding.tvGeneroMasVisto.setText(generoLabel);

        //gráfico de barras por mes del año
        configurarGrafico(lista);
    }

    private void configurarGrafico(List<SeguimientoFS> lista) {
        int[] conteoMensual = new int[12];
        String anioActual = String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));

        for (SeguimientoFS s : lista) {
            String fecha = s.getFechaVisualizacion();
            if (fecha != null && fecha.startsWith(anioActual) && fecha.length() >= 7) {
                try {
                    int mes = Integer.parseInt(fecha.substring(5, 7));
                    conteoMensual[mes - 1]++;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        //Barras
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            barEntries.add(new BarEntry(i, conteoMensual[i]));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColor(Color.parseColor("#FFCC80"));
        barDataSet.setValueTextColor(Color.DKGRAY);
        barDataSet.setValueTextSize(9f);
        barDataSet.setDrawValues(true);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        //Línea de tendencia
        List<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            lineEntries.add(new Entry(i, conteoMensual[i]));
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setColor(Color.parseColor("#FF6F00"));
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleColor(Color.parseColor("#FF6F00"));
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setCircleHoleColor(Color.WHITE);
        lineDataSet.setCircleHoleRadius(2f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.parseColor("#FFCC80"));
        lineDataSet.setFillAlpha(60);

        //Combinar ls dos
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(new LineData(lineDataSet));

        CombinedChart chart = binding.combinedChart;
        chart.setData(combinedData);
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.LINE
        });

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setExtraBottomOffset(8f);

        // Eje Y
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setTextColor(Color.LTGRAY);
        chart.getAxisLeft().setGridColor(Color.parseColor("#F0F0F0"));

        // Eje X
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(meses));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(12);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(9f);

        chart.animateY(800);
        chart.invalidate();
    }

    //Este método es símplemente para formatear la fecha
    private String formatearMes(String mesAnio) {
        try {
            String[] partes = mesAnio.split("-");
            int mes = Integer.parseInt(partes[1]);
            String anio = partes[0];
            String[] nombresMeses = getResources().getStringArray(R.array.mesesCompletos);
            return nombresMeses[mes - 1] + " " + anio;
        } catch (Exception e) {
            return mesAnio;
        }
    }

    //Parseo de fechas
    private Calendar parseFecha(String fechaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(fechaStr));
            return cal;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
