import { AreaChart } from "@mantine/charts";

import { appointmentData, doctorData, patientData } from "../../../Data/DashboardData";
import { IconFile,IconStethoscope, IconUser } from "@tabler/icons-react";
import { ThemeIcon } from "@mantine/core";
import React, {  useEffect, useState } from "react";
import { countAllAppointments } from "../../../Service/AppointmentService";
import { addZeroMonths } from "../../../Utility/OtherUtility";
import { getRegistrationCounts } from "../../../Service/UserService";

const TopCards = () => {

  const [apData,setApData]=useState<any[]>(appointmentData);
  const [drData,setDrData]=useState<any[]>(doctorData);
  const [ptData,setPtData]=useState<any[]>(patientData);


  useEffect(()=>{

    countAllAppointments().then((res)=>{
      
      setApData(addZeroMonths(res,"month","count"));
    }).catch((err)=>{
      console.error("Error fetching appointment counts:", err);
    });
    

    getRegistrationCounts().then((res)=>{
      setPtData(addZeroMonths(res.patientCounts,"month","count"));
      setDrData(addZeroMonths(res.doctorCounts,"month","count"));
    }).catch((err)=>{
      console.error("Error fetching registration counts:", err);
    });


  },[]);



  const getSum = (data: any[], key: string) => {
    return data.reduce((sum, item) => sum + item[key], 0);
  }
  const card = (
    name: string,
    id: string,
    color: string,
    bg: string,
    icon: React.ReactNode,
    data: any[]
  ) => {
    return (
      <div className={`${bg} rounded-xl `}>
        <div className="flex justify-between p-5 items-center gap-5">
          <ThemeIcon size="xl" radius="md" color={color}>
            {icon}
          </ThemeIcon>
          <div className="flex flex-col font-medium items-end">
            <div>{name}</div>
            <div className="text-lg">{getSum(data, id)}</div>
          </div>
        </div>
        <AreaChart
        strokeWidth={4}
        withGradient
        fillOpacity={0.70}
          h={100}
          data={data}
          dataKey="month"
          series={[{ name:  id , color:  color  }]}
          curveType="bump"
          tickLine="none"
          gridAxis="none"
          withXAxis={false}
          withYAxis={false}
          withDots={false}
        />
      </div>
    );
  };
  const cards=[
    {
      name:"Appointments",
      id:"count",
      color:"blue",
      bg:"bg-blue-100",
      icon:<IconFile/>,
      data: apData
    },
    {
      name:"Patients",
      id:"count",
      color:"green",
      bg:"bg-green-100",
      icon:<IconUser/>, 
      data: ptData

    },
    {
      name:"Doctors",
      id:"count",
      color:"red",
      bg:"bg-red-100",
      icon:<IconStethoscope/>,
      data: drData
    }
  ]
  return <div className="grid lg:grid-cols-3 gap-5 ">{cards.map((cardData) => card(cardData.name, cardData.id, cardData.color, cardData.bg, cardData.icon, cardData.data))}</div>;
};

export default TopCards;
