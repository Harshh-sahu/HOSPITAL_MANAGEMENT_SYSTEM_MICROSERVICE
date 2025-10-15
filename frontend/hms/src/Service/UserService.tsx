import axiosInstance from "../Interceptor/AxiosInterceptor";

const registerUser = async (user: any) => {
  return axiosInstance
    .post("/user/register", user)

    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during registration:", error);
      throw error;
    });
};


const loginUser = async (user: any) => {
  return axiosInstance
    .post("/user/login", user)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error during login:", error);
      throw error;
    });
};

const getUserProfile = async (id: any) => {
  return axiosInstance
    .get("/user/getProfile/" + id)
    .then((response: any) => response.data)
    .catch((error: any) => {
      console.error("Error fetching user profile:", error);
      throw error;
    });
};

export { registerUser, loginUser, getUserProfile };
