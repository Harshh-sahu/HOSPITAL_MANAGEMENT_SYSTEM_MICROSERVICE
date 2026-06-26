import React, { useEffect } from 'react'
import { getAllDoctor } from '../../../Service/DoctorProfileService'
import DoctorCard from './DoctorCard';
import { useMediaQuery } from '@mantine/hooks';
import { Button, Group } from '@mantine/core';
import { IconDownload, IconFileTypePdf } from '@tabler/icons-react';
import { exportToCSV, generateDoctorReportPDF } from '../../../Utility/ExportUtil';

const Doctor = () => {
  const matches = useMediaQuery('(max-width: 768px)');
  const [Doctor, setDoctor] = React.useState<any[]>([]);

  useEffect(() => {
    getAllDoctor().then((res) => {
      setDoctor(res);
    }).catch((error) => {
      console.error(error);
    });
  }, []);

  const handleExportCSV = () => {
    exportToCSV(
      "Doctor_Export",
      ["Name", "Email", "Phone", "Department", "Specialization", "Address", "Experience (yrs)"],
      Doctor.map((d) => [
        d.name ?? "",
        d.email ?? "",
        d.phone ?? "",
        d.department ?? "",
        d.specialization ?? "",
        d.address ?? "",
        d.totalExp ?? "",
      ])
    );
  };

  return (
    <div className='p-5'>
      <div className='flex items-center justify-between mb-5'>
        <div className='text-xl text-primary-500 font-semibold'>Doctors ({Doctor.length})</div>
        <Group gap="xs">
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
            gradient={{ from: "blue", to: "violet" }}
            leftSection={<IconFileTypePdf size={16} />}
            onClick={() => generateDoctorReportPDF(Doctor)}
          >
            Export PDF
          </Button>
        </Group>
      </div>

      {Doctor.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 gap-3">
          <div className="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center">
            <IconDownload size={32} stroke={1.2} className="text-gray-300" />
          </div>
          <p className="text-gray-400 font-medium">No doctors registered yet.</p>
        </div>
      ) : (
        <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-cols-2 grid-cols-1 gap-5">
          {Doctor.map((doc) => (
            <DoctorCard key={doc.id} {...doc} />
          ))}
        </div>
      )}
    </div>
  );
};

export default Doctor;

