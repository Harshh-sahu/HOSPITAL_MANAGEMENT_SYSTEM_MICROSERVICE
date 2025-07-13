import Header from "../Components/Header/Header";
import Sidebar from "../Components/Sidebar/Sidebar";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Random from "../Components/Random";
import AdminDashboard from "../Pages/AdminDashboard";
import LoginPage from "../Pages/LoginPage";
import RegisterPage from "../Pages/RegisterPage";
const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login page */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Admin dashboard layout with nested routes */}
        <Route path="/" element={<AdminDashboard />}>
          <Route path="dashboard" element={<Random />} />
          <Route path="pharmacy" element={<Random />} />
          <Route path="patients" element={<Random />} />
          <Route path="doctors" element={<Random />} />
          <Route path="appointments" element={<Random />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default AppRoutes;
