import React, { useEffect } from 'react'
import { getAllPatient } from '../../../Service/PatientProfileService'
import PatientCard from './PatientCard';
import { Button, Group, Text } from '@mantine/core';
import { IconDownload, IconFileTypePdf } from '@tabler/icons-react';
import { bloodGroupMap } from '../../../Data/DropDownData';
import { exportToCSV, generatePatientReportPDF } from '../../../Utility/ExportUtil';

const Patient = () => {

    useEffect(()=>{
getAllPatient().then((res)=>{
    console.log(res);
    setPatient(res);
}).catch((error)=>{
    console.error(error);
});
    },[])
    const [patient,setPatient] = React.useState<any[]>([]);

  const handleExportCSV = () => {
    const headers = ["Name", "Email", "Phone", "DOB", "Blood Group", "Address", "Allergies", "Chronic Disease"];
    const rows = patient.map((p: any) => [
      p.name ?? "",
      p.email ?? "",
      p.phone ?? "",
      p.dob ?? "",
      bloodGroupMap[p.bloodGroup] ?? p.bloodGroup ?? "",
      p.address ?? "",
      p.allergies ?? "",
      p.chronicDisease ?? "",
    ]);
    exportToCSV(`Patient_List`, headers, rows);
  };

  return (
    <div>
      <Group justify="space-between" align="center" mb="md">
        <Text size="xl" fw={600} className="text-primary-500">Patients</Text>
        <Group gap={8}>
          <Button
            size="sm"
            variant="light"
            color="blue"
            leftSection={<IconDownload size={16} />}
            onClick={handleExportCSV}
          >
            Export CSV
          </Button>
          <Button
            size="sm"
            variant="gradient"
            gradient={{ from: "blue", to: "indigo" }}
            leftSection={<IconFileTypePdf size={16} />}
            onClick={() => generatePatientReportPDF(patient, bloodGroupMap)}
          >
            Export PDF
          </Button>
        </Group>
      </Group>

      <div className='grid lg:grid-cols-4 md:grid-cols-3 sm:grid-cols-2 grid-cols-1 gap-5'>
        {patient.map((p) => (
          <PatientCard key={p.id} {...p} />
        ))}
      </div>
    </div>
  )
}

export default Patient


// <div className='p-5'>
       
//        <div className='text-xl  mb-5 text-primary-500 font-semibold'>
//           Doctor
//        </div>


//        <div className='grid grid-cols-4 gap-5 '>
//         {Doctor.map((Doctor) => (
//         <DoctorCard  key={Doctor.id} {...Doctor}   />
//        ))}
//        </div>
        
        
//     </div>