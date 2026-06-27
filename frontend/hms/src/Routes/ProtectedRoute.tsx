import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import { isTokenValid } from "../Slices/JwtSlice";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const token = useSelector((state: any) => state.jwt);

  if (isTokenValid(token)) {
    return <>{children}</>;
  }
  return <Navigate to="/login" replace />;
};

export default ProtectedRoute;
