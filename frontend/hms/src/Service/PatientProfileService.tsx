import axiosInstance from "../Interceptor/AxiosInterceptor";


const getPatient = async (id: any) => {
  return axiosInstance
    .get("/profile/patient/get/"+id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during login:", error);
      throw error;
    });
};

const updatePatient = async (patient: any) =>{

    return axiosInstance.put("/profile/patient/update", patient)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during update:", error);
      throw error;
    });
}


const getAllPatient =async()=>{
  return axiosInstance.get("/profile/patient/getAll")
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during getAll:", error);
    throw error;
  });
}

export {getPatient , updatePatient , getAllPatient};