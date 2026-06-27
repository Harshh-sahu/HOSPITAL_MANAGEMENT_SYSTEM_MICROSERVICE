import axiosInstance from "../Interceptor/AxiosInterceptor";

const getAdmin = async (userId: any) => {
  return axiosInstance
    .get("/profile/admin/getByUserId/" + userId)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error fetching admin profile:", error);
      throw error;
    });
};

const updateAdmin = async (admin: any) => {
  return axiosInstance
    .put("/profile/admin/update", admin)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error updating admin profile:", error);
      throw error;
    });
};

export { getAdmin, updateAdmin };
