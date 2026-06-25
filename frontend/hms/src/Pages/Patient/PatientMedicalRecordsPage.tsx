import MedicalRecords from "../../Components/Patient/MedicalRecords/MedicalRecords";

const PatientMedicalRecordsPage = () => {
  return (
    <div className="p-5">
      <h2 className="text-2xl font-semibold text-primary-600 mb-5">Medical Records</h2>
      <MedicalRecords />
    </div>
  );
};

export default PatientMedicalRecordsPage;
