import axiosInstance from "../Interceptor/AxiosInterceptor";

const uploadMedia = async (file: any) => {

    const formData = new FormData();
    formData.append("file", file);
  return axiosInstance
    .post("/media/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })

    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during adding media:", error);
      throw error;
    });
};

const getMedia = async (id: any) => {
  return axiosInstance
    .get("/media/" + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during fetching media:", error);
      throw error;
    });
};

export { uploadMedia, getMedia };