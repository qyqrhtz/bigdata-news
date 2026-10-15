<template>
  <div class="chart-container">
    <div class="chart-title">{{ title }}</div>
    <div ref="chartRef" :style="{ height: height }"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  title: String,
  type: String,
  chartData: Array,
  height: { default: '240px' }
})

const chartRef = ref(null)
let myChart = null

const initChart = () => {
  if (!chartRef.value) return
  if (!myChart) {
    myChart = echarts.init(chartRef.value, 'dark')
  }

  const xData = props.chartData ? props.chartData.map(d => d.name) : []
  const yData = props.chartData ? props.chartData.map(d => d.value) : []

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: props.type === 'pie' || props.type === 'doughnut' ? 'item' : 'axis',
      backgroundColor: 'rgba(0, 28, 55, 0.9)',
      borderColor: '#00d2ff',
      textStyle: { color: '#fff' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: (props.type === 'pie' || props.type === 'doughnut') ? null : {
      type: 'category',
      data: xData,
      axisLabel: {
        color: '#ccc',
        interval: 0,
        rotate: xData.length > 5 ? 30 : 0
      },
      axisLine: {
        lineStyle: { color: '#1a3a63' }
      }
    },
    yAxis: (props.type === 'pie' || props.type === 'doughnut') ? null : {
      type: 'value',
      axisLabel: {
        color: '#ccc'
      },
      axisLine: {
        lineStyle: { color: '#1a3a63' }
      },
      splitLine: {
        lineStyle: { color: 'rgba(26, 58, 99, 0.5)' }
      }
    },
    series: [{
      name: props.title,
      data: props.type === 'pie' || props.type === 'doughnut' ? props.chartData : yData,
      type: props.type === 'categoryBar' ? 'bar' : (props.type === 'doughnut' ? 'pie' : props.type),
      radius: props.type === 'doughnut' ? ['45%', '70%'] : (props.type === 'pie' ? '60%' : null),
      itemStyle: {
        borderRadius: 4,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#00d2ff' },
          { offset: 1, color: '#3a7bd5' }
        ])
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 210, 255, 0.5)'
        }
      }
    }]
  }

  myChart.setOption(option)
}

watch(() => props.chartData, () => {
  initChart()
}, { deep: true })

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => {
    myChart?.resize()
  })
})
</script>

<style scoped>
.chart-container {
  background: rgba(26, 58, 99, 0.3);
  border: 1px solid rgba(0, 210, 255, 0.2);
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 210, 255, 0.1);
}

.chart-title {
  font-size: 14px;
  font-weight: bold;
  color: #00d2ff;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #00d2ff;
}
</style>
