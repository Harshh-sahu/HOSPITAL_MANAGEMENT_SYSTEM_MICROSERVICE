
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Random from "../Components/Random";
import AdminDashboard from "../Layout/AdminDashboard";
import LoginPage from "../Pages/LoginPage";
import PatientProfilePage from "../Pages/Patient/PateintProfilePage";
import RegisterPage from "../Pages/RegisterPage";
import PublicRoute from "./PublicRoute";
import ProtectedRoute from "./ProtectedRoute";
import PatientDashboard from "../Layout/PatientDashboard";
import DoctorDashboard from "../Layout/DoctorDashboard";
import DoctorProfilePage from "../Pages/Doctor/DoctorProfilePage";
import PatientAppointmentPage from "../Pages/Patient/PatientAppointmentPage";
import DoctorAppointmentPage from "../Pages/Doctor/DoctorAppointmentPage";
const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login page */}
        <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />

        {/* Admin dashboard layout with nested routes */}
        <Route path="/" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>}>
          <Route path="/dashboard" element={<Random />} />
          <Route path="/pharmacy" element={<Random />} />
          <Route path="/patients" element={<Random />} />
          <Route path="/doctors" element={<Random />} />
       
        </Route>
        <Route path="/doctor" element={<ProtectedRoute><DoctorDashboard /></ProtectedRoute>}>
          <Route path="dashboard" element={<Random />} />
                    <Route path="profile" element={<DoctorProfilePage/>} />

          <Route path="pharmacy" element={<Random />} />
          <Route path="patients" element={<Random />} />
                <Route path="appointment" element={<DoctorAppointmentPage />} />
          <Route path="doctors" element={<Random />} />

        </Route>
            <Route path="patient" element={<ProtectedRoute><PatientDashboard /></ProtectedRoute>}>

          <Route path="dashboard" element={<Random />} />
          <Route path="profile" element={<PatientProfilePage/>} />
          <Route path="appointment" element={<PatientAppointmentPage />} />          
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default AppRoutes;
