import { DonutChart } from '@mantine/charts'
import  { useEffect, useState } from 'react'
import { diseaseData } from '../../../Data/DashboardData'
import {  countReasonByPatient } from '../../../Service/AppointmentService'
import { convertReasonChartData } from '../../../Utility/OtherUtility'
import { useSelector } from 'react-redux'

const DiseaseChart = () => {
const [data,setData] = useState<any[]>
(diseaseData);

const user = useSelector((state:any)=>state.user);
  useEffect(()=>{
    countReasonByPatient(user.profileId).then((res)=>{
      setData(convertReasonChartData(res));
    }).catch((err)=>{
      console.error("Error fetching disease reason data:", err);
    });
  },[])
  return (
    <div className='p-3 border rounded-xl bg-green-50 shadow-xl flex flex-col gap-3'>
        <div className='text-xl font-semibold'>
            Reason Distribution
        </div>

        <div className='flex justify-center'>
               <DonutChart withLabelsLine labelsType="percent" withLabels data={data}
              chartLabel="Disease" 
              thickness={25}
              size={200}
              paddingAngle={10} />

        </div>
      
      
    </div>
  )
}

export default DiseaseChart
