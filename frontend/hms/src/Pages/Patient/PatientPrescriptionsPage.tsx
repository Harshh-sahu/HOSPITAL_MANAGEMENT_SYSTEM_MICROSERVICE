import Prescriptions from "../../Components/Patient/Prescriptions/Prescriptions";

const PatientPrescriptionsPage = () => {
  return (
    <div className="p-5">
      <h2 className="text-2xl font-semibold text-primary-600 mb-5">Prescriptions</h2>
      <Prescriptions />
    </div>
  );
};

export default PatientPrescriptionsPage;
