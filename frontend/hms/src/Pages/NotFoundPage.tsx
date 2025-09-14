import { Button } from "@mantine/core";
import { motion } from "framer-motion";
import { Link, useNavigate } from "react-router-dom";


export default function NotFoundPage() {
    const navigate = useNavigate();
  return (
    <div className="flex items-center justify-center h-screen bg-gradient-to-r from-gray-900 via-gray-800 to-black text-white">
      <motion.div
        className="text-center"
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        {/* Big 404 */}
        <motion.h1
          className="text-9xl font-extrabold tracking-widest text-red-500 drop-shadow-lg"
          initial={{ scale: 0.8 }}
          animate={{ scale: 1 }}
          transition={{ duration: 0.6 }}
        >
          404
        </motion.h1>

        {/* Message */}
        <p className="mt-4 text-xl md:text-2xl text-gray-300">
          Oops! The page you are looking for does not exist.
        </p>

        {/* Button */}
        <div className="mt-8">
          <Button
            onClick={() => navigate(-1)}
            className="px-6 py-3 text-lg font-semibold rounded-2xl bg-red-500 hover:bg-red-600 transition-colors shadow-lg"
          >
            Go Back Home
          </Button>
        </div>

        {/* Small Note */}
        <p className="mt-6 text-sm text-gray-500">
          Or maybe you mistyped the URL?
        </p>
      </motion.div>
    </div>
  );
}
