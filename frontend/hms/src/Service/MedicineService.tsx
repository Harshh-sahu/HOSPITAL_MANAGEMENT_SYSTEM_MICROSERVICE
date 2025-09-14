import axiosInstance from "../Interceptor/AxiosInterceptor";

const addMedicine = async (data: any) => {
  return axiosInstance
    .post("/pharmacy/medicines/add", data)

    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during adding medicine:", error);
      throw error;
    });
};

const getMedicines = async (id: any) => {
  return axiosInstance
    .get("/pharmacy/medicines/get" + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching getmedicines:", error);
      throw error;
    });
};

const getAllMedicines = async () => {
  return axiosInstance
    .get("/pharmacy/medicines/getAll")
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching all medicines:", error);
      throw error;
    });
};

const updateMedicine = async (id: any, data: any) => {
  return axiosInstance
    .put("/pharmacy/medicines/update", data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during updating medicine:", error);
      throw error;
    });
};

export { addMedicine, getMedicines, getAllMedicines, updateMedicine };
