import axios from "axios";

const chatAxios = axios.create({
  baseURL: "http://localhost:8090",
});

chatAxios.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface ChatResponse {
  response: string;
  conversationId: string;
  timestamp: string;
}

export const sendChatMessage = async (
  message: string,
  conversationId: string
): Promise<ChatResponse> => {
  return chatAxios
    .post("/chatbot/message", { message, conversationId })
    .then((res) => res.data)
    .catch((error) => {
      console.error("Error sending chat message:", error);
      throw error;
    });
};

export const clearChatHistory = async (conversationId: string): Promise<void> => {
  return chatAxios
    .delete(`/chatbot/history/${conversationId}`)
    .then(() => {})
    .catch((error) => {
      console.error("Error clearing chat history:", error);
      throw error;
    });
};
