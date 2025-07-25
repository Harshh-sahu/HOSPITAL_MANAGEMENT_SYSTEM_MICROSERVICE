
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Random from "../Components/Random";
import AdminDashboard from "../Pages/AdminDashboard";
import LoginPage from "../Pages/LoginPage";
import RegisterPage from "../Pages/RegisterPage";
import PublicRoute from "./PublicRoute";
import ProtectedRoute from "./ProtectedRoute";
const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login page */}
        <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />

        {/* Admin dashboard layout with nested routes */}
        <Route path="/" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>}>
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
