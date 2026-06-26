import { DonutChart } from '@mantine/charts'
import React, { useEffect, useState } from 'react'
import { countAllReasons } from '../../../Service/AppointmentService'
import { convertReasonChartData } from '../../../Utility/OtherUtility'
import { Center, Text } from '@mantine/core'
import { IconChartDonut } from '@tabler/icons-react'

const DiseaseChart = () => {
const [data,setData] = useState<any[]>([]);
  useEffect(()=>{
    countAllReasons().then((res)=>{
      setData(convertReasonChartData(res));
    }).catch((err)=>{
      console.error("Error fetching disease reason data:", err);
    });
  },[])
  return (
    <div className='p-3 border rounded-xl bg-green-50 shadow-xl flex flex-col gap-3'>
        <div className='text-xl font-semibold'>Reason Distribution</div>
        <div className='flex justify-center'>
          {data.length === 0 ? (
            <Center h={200}>
              <div className="flex flex-col items-center gap-2">
                <IconChartDonut size={48} stroke={1.2} className="text-gray-300" />
                <Text c="dimmed" size="sm">No visit reason data yet.</Text>
              </div>
            </Center>
          ) : (
            <DonutChart withLabelsLine labelsType="percent" withLabels data={data}
              chartLabel="Disease" thickness={25} size={200} paddingAngle={10} />
          )}
        </div>
    </div>
  )
}

export default DiseaseChart
