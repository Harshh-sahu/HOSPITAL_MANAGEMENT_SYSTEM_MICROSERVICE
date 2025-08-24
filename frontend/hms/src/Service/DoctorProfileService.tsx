import axiosInstance from "../Interceptor/AxiosInterceptor";


const getDoctor = async (id: any) => {
  return axiosInstance
    .get("/profile/doctor/get/"+id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during login:", error);
      throw error;
    });
};

const updateDoctor = async (doctor: any) =>{

    return axiosInstance.put("/profile/doctor/update", doctor)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during update:", error);
      throw error;
    });
}

const getDoctorDropdown = async()=>{
  return axiosInstance
    .get("/profile/doctor/dropdowns")
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error fetching doctor dropdown:", error);
      throw error;
    });
}

export {getDoctor , updateDoctor, getDoctorDropdown};