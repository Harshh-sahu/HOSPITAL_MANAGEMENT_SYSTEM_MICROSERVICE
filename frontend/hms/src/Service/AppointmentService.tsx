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

const countAppointmentByPatient = async(patientId:any)=>{
  return axiosInstance
  .get("/appointment/countByPatient/"+patientId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during counting appointments by patient:", error);
    throw error;
  });
}

const countAppointmentByDoctor = async(doctorId:any)=>{
  return axiosInstance
  .get("/appointment/countByDoctor/"+doctorId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during counting appointments by doctor:", error);
    throw error;
  });
}

const countAllAppointments = async()=>{
  return axiosInstance
  .get("/appointment/visitCount") 
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during counting all appointments:", error);
    throw error;
  });
}

const countReasonByPatient = async(patientId:any)=>{
  return axiosInstance
  .get("/appointment/countReasonByPatient/"+patientId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during counting reason by patient:", error);
    throw error;
  });
};

const countReasonByDoctor = async(doctorId:any)=>{
  return axiosInstance
  .get("/appointment/countReasonByDoctor/"+doctorId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during counting reason by doctor:", error);
    throw error;
  });
};


const countAllReasons = async()=>{
  return axiosInstance
  .get("/appointment/countReasons")
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during counting all reasons:", error);
    throw error;
  });
};


const getMedicineConsumeByPatient = async(patientId:any)=>{

  return axiosInstance
  .get("/appointment/getMedicinesByPatient/"+patientId)
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during fetching medicine consumption by patient ID:", error);
    throw error;
  });
};

const getTodaysAppointments = async()=>{

  return axiosInstance
  .get("/appointment/today")
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during fetching today's appointments:", error);
    throw error;
  });
};

export { scheduleAppointment, cancelAppointment, getAppointment, getAppointmentDetails, getAppointmentByPatient, getAppointmentByDoctor, createAppointmentReport, isReportExists, getReportByPatientId , getPrescriptionByPatientId ,getAllPrescriptions, getMedicinesByPrescriptionId, countAppointmentByPatient , countAppointmentByDoctor,
  countAllAppointments,
  countReasonByPatient,
  countAllReasons,
  countReasonByDoctor,
  getMedicineConsumeByPatient,
  getTodaysAppointments
};
