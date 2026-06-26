import React, { useEffect } from 'react'
import { getAllPatient } from '../../../Service/PatientProfileService'
import PatientCard from './PatientCard';
import { useMediaQuery } from '@mantine/hooks';
import { Button, Group } from '@mantine/core';
import { IconDownload, IconFileTypePdf } from '@tabler/icons-react';
import { exportToCSV, generatePatientReportPDF } from '../../../Utility/ExportUtil';
import { bloodGroupMap } from '../../../Data/DropDownData';

const Patient = () => {
  const matches = useMediaQuery('(max-width: 768px)');
  const [patient, setPatient] = React.useState<any[]>([]);

  useEffect(() => {
    getAllPatient().then((res) => {
      setPatient(res);
    }).catch((error) => {
      console.error(error);
    });
  }, []);

  const handleExportCSV = () => {
    exportToCSV(
      "Patient_Export",
      ["Name", "Email", "Phone", "DOB", "Blood Group", "Address", "Allergies", "Chronic Disease"],
      patient.map((p) => [
        p.name ?? "",
        p.email ?? "",
        p.phone ?? "",
        p.dob ?? "",
        bloodGroupMap[p.bloodGroup] ?? p.bloodGroup ?? "",
        p.address ?? "",
        p.allergies ?? "",
        p.chronicDisease ?? "",
      ])
    );
  };

  return (
    <div>
      <div className='flex items-center justify-between mb-5'>
        <div className='text-xl text-primary-500 font-semibold'>Patients ({patient.length})</div>
        <Group gap="xs">
          <Button
            size="sm"
            variant="light"
            color="green"
            leftSection={<IconDownload size={16} />}
            onClick={handleExportCSV}
          >
            Export CSV
          </Button>
          <Button
            size="sm"
            variant="gradient"
            gradient={{ from: "green", to: "teal" }}
            leftSection={<IconFileTypePdf size={16} />}
            onClick={() => generatePatientReportPDF(patient, bloodGroupMap)}
          >
            Export PDF
          </Button>
        </Group>
      </div>

      {patient.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 gap-3">
          <div className="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center">
            <IconDownload size={32} stroke={1.2} className="text-gray-300" />
          </div>
          <p className="text-gray-400 font-medium">No patients registered yet.</p>
        </div>
      ) : (
        <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-cols-2 grid-cols-1 gap-5">
          {patient.map((p) => (
            <PatientCard key={p.id} {...p} />
          ))}
        </div>
      )}
    </div>
  );
};

export default Patient;
