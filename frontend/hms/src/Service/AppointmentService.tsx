import axiosInstance from "../Interceptor/AxiosInterceptor";

const scheduleAppointment = async (data: any) => {
  return axiosInstance
    .post("/appointment/schedule", data)

    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during scheduling appointment:", error);
      throw error;
    });
};


const cancelAppointment = async (id: any) => {
  return axiosInstance
    .put("/appointment/cancel/"+id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during cancel appointment:", error);
      throw error;
    });
};

const getAppointment = async (id: any) => {
    return axiosInstance.get("/appointment/get/"+id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching appointment:", error);
      throw error;
    });
}
const getAppointmentDetails= async (id: any) => {
    return axiosInstance.get("/appointment/get/details/"+id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching appointment details:", error);
      throw error;
    });
};
const getAppointmentByPatient = async (patientId: any) => {
    return axiosInstance.get("/appointment/getAllByPatient/"+patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching all appointments:", error);
      throw error;
    });
};

const getAppointmentByDoctor= async (doctorId:any)=>{
  return axiosInstance.get("/appointment/getAllByDoctor/"+doctorId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during fetching doctor all appointments:", error);
    throw error;
  });
}


const createAppointmentReport = async (data: any) => {
  return axiosInstance
    .post("/appointment/report/create", data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during creating appointment:", error);
      throw error;
    });
};

const isReportExists = async (appointmentId: any) => {
  return axiosInstance
    .get("/appointment/report/isRecordExists/" + appointmentId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during checking report existence:", error);
      throw error;
    });
};

const getReportByPatientId = async (patientId: any) => {
  return axiosInstance
    .get("/appointment/report/getRecordsByPatientId/" + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching report by patient ID:", error);
      throw error;
    });
};

const getPrescriptionByPatientId = async (patientId: any) => {
  return axiosInstance
    .get("/appointment/report/getPrescriptionsByPatientId/" + patientId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching prescription by patient ID:", error);
      throw error;
    });
};


const getAllPrescriptions = async()=>{

  return axiosInstance
  .get("/appointment/report/getAllPrescriptions")
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during fetching all prescriptions:", error);
    throw error;
  });
}

const getMedicinesByPrescriptionId = async(prescriptionId:any)=>{

  return axiosInstance
  .get("/appointment/report/getMedicinesByPrescriptionId/"+prescriptionId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during fetching medicines by prescription ID:", error);
    throw error;
  });
}

export { scheduleAppointment, cancelAppointment, getAppointment, getAppointmentDetails, getAppointmentByPatient, getAppointmentByDoctor, createAppointmentReport, isReportExists, getReportByPatientId , getPrescriptionByPatientId ,getAllPrescriptions, getMedicinesByPrescriptionId};
