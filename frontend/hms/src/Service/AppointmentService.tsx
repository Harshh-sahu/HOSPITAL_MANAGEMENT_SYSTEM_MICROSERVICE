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

export { scheduleAppointment, cancelAppointment, getAppointment, getAppointmentDetails, getAppointmentByPatient };
