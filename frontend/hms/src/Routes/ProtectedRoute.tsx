import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

interface ProtectedRouteProps {
  children: React.ReactNode;  // ✅ fixed typo
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const token = useSelector((state: any) => state.jwt);

  if (token) {
    return <>{children}</>;
  }
    return <Navigate to="/login" />;

    // ✅ wrapped in fragment
};

export default ProtectedRoute;
