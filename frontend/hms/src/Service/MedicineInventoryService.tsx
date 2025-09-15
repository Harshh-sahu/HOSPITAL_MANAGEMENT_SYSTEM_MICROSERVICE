import axiosInstance from "../Interceptor/AxiosInterceptor";

const addStock = async (data: any) => {
  return axiosInstance
    .post("/pharmacy/inventory/add", data)

    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during adding stock:", error);
      throw error;
    });
};

const getStock = async (id: any) => {
  return axiosInstance
    .get("/pharmacy/inventory/get" + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching getStock:", error);
      throw error;
    });
};

const getAllStock = async () => {
  return axiosInstance
    .get("/pharmacy/inventory/getAll")
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching all stock:", error);
      throw error;
    });
};

const updateStock  = async (id: any, data: any) => {
  return axiosInstance
    .put("/pharmacy/inventory/update", data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during updating stock:", error);
      throw error;
    });
};

export { addStock, getStock, getAllStock, updateStock };
