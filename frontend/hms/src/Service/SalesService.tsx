import axiosInstance from "../Interceptor/AxiosInterceptor";

const addSale = async (data: any) => {
  return axiosInstance
    .post("/pharmacy/sales/create", data)

    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during adding Sale:", error);
      throw error;
    });
};

const getSale = async (id: any) => {
  return axiosInstance
    .get("/pharmacy/sales/get" + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching getSale:", error);
      throw error;
    });
};

const getAllSaleItem = async (id:any) => {
  return axiosInstance
    .get("/pharmacy/sales/getSaleItems/"+id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching all Sale:", error);
      throw error;
    });
};

const updateSale  = async (id: any, data: any) => {
  return axiosInstance
    .put("/pharmacy/sales/update", data)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during updating Sale:", error);
      throw error;
    });
};


const getAllsales = async () =>{
  return axiosInstance
  .get("/pharmacy/sales/getAll")
  .then((response: any) => response.data)
  .catch((error: any) => {
    console.error("Error during fetching all sales:", error);
    throw error;
  });
}

export { addSale, getSale, getAllSaleItem, updateSale, getAllsales };
