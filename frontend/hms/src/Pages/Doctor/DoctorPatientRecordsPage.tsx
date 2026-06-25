import { Group } from "@mantine/core";
import { IconFolderOpen } from "@tabler/icons-react";
import PatientRecords from "../../Components/Doctor/PatientRecords/PatientRecords";

const DoctorPatientRecordsPage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconFolderOpen size={26} className="text-primary-500" />
        <h2 className="text-2xl font-semibold text-primary-600">Patient Records</h2>
      </Group>
      <PatientRecords />
    </div>
  );
};

export default DoctorPatientRecordsPage;
