import { Button, PasswordInput, TextInput } from "@mantine/core";
import { IconHeartbeat } from "@tabler/icons-react";
import React from "react";
import { Link, useNavigate } from "react-router-dom";

import { useForm } from "@mantine/form";
import { errorNotification, successNotification } from "../Utility/NotificationUtil";
import { loginUser } from "../Service/UserService";

// TODO: You need to define or import the 'loginUser' function.
// For example: import { loginUser } from "../api/auth";

const LoginPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = React.useState(false);
  const form = useForm({
    initialValues: {
      email: "",
      password: "",
    },

    validate: {
      email: (value: string) =>
        /^\S+@\S+$/.test(value) ? null : "Invalid email",
      password: (value: string) =>
        value.length >= 6
          ? null
          : "Password must be at least 6 characters long",
    },
  });


  const handleSubmit = (values: typeof form.values) => {
    console.log("Submitting login with:", values);
    setLoading(true);
    loginUser(values)
      .then((data) => {
        console.log("Login Success:", data);
        localStorage.setItem("token", data.token); // ✅ optional

        successNotification("Login successful!");
        navigate("/dashboard"); // ✅ go to dashboard
      })
      .catch((error) => {
        console.error("Login Failed:", error);
        errorNotification(
          error?.response?.data?.errorMessage || "An unknown error occurred."
        );
      }).finally(()=> setLoading(false));
  };


  return (
    <div
      style={{ background: 'url("/hs.jpg")' }}
      className="h-screen !bg-no-repeat !bg-cover !bg-center w-screen flex flex-col items-center justify-center"
    >
      <div className="py-3 flex text-pink-500 gap-1">
        <IconHeartbeat size={45} stroke={2.5} />
        <span className="font-handling font-semibold text-4xl">Pulse</span>
      </div>

      <div className="w-[450px] backdrop-blur-md p-10 py-8 rounded-lg">
        <form
          onSubmit={form.onSubmit(handleSubmit)}
          // Corrected the Tailwind CSS arbitrary variant syntax here
          className="flex flex-col gap-5 [&_input]:placeholder-black [&_.mantine-Input-input]:!border-white focus-within:[&_.mantine-Input-input]:!border-pink-400 [&_.mantine-Input-input]:!border [&_input]:pl-2 [&_svg]:text-black [&_input]:!text-white"
        >
          <div className="self-center font-medium bg-fontFamily-heading text-white text-xl">
            Login
          </div>
          <TextInput
            className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Email"
            {...form.getInputProps("email")}
          />
          <PasswordInput
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Password"
            className="transition duration-300"
            {...form.getInputProps("password")}
          />
          <Button loading={loading} radius="md" size="md" type="submit" color="pink">
            Login
          </Button>
          <div className="text-sm self-center text-white">
            Don't have an account?{" "}
            <Link
              to="/register"
              className="hover:underline hover:text-blue-400"
            >
              Register
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;