import { AreaChart } from "@mantine/charts";

import { appointmentData, doctorData, patientData } from "../../../Data/DashboardData";
import { IconFile, IconPhoto, IconStethoscope, IconUser } from "@tabler/icons-react";
import { ThemeIcon } from "@mantine/core";
import { ReactNode } from "react";

const TopCards = () => {

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
          dataKey="date"
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
      id:"appointments",
      color:"blue",
      bg:"bg-blue-100",
      icon:<IconFile/>,
      data: appointmentData
    },
    {
      name:"Patients",
      id:"patients",
      color:"green",
      bg:"bg-green-100",
      icon:<IconUser/>, 
      data: patientData

    },
    {
      name:"Doctors",
      id:"doctors",
      color:"red",
      bg:"bg-red-100",
      icon:<IconStethoscope/>,
      data: doctorData
    }
  ]
  return <div className="grid grid-cols-3 gap-5 ">{cards.map((cardData) => card(cardData.name, cardData.id, cardData.color, cardData.bg, cardData.icon, cardData.data))}</div>;
};

export default TopCards;
