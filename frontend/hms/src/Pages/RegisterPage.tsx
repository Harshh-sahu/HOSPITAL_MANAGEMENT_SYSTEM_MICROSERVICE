import { Button, PasswordInput, SegmentedControl, TextInput } from "@mantine/core";
import { IconHeartbeat } from "@tabler/icons-react";

import { useForm } from '@mantine/form';
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../Service/UserService";
import { errorNotification, successNotification,  } from "../Utility/NotificationUtil";
import React from "react";


const RegisterPage = () => {
const navigate = useNavigate();
  const [loading, setLoading] = React.useState(false);

 const form = useForm({

    initialValues: {
      name: '',
        role:"PATIENT",
      email: '',
      password: '',
      confirmPassword: '',
    },

    validate: {
      name:(value: string)=> (!value? 'Name is required' : null),
  email: (value: string) =>
    /^\S+@\S+$/.test(value) ? null : 'Invalid email',

password: (value: string) =>
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/.test(value)
    ? null
    : 'Password must be 8-15 characters long, include upper, lower, number & special character',


confirmPassword: (value: string, values: any) =>
    value === values.password ? null : 'Passwords do not match',
},  });

   const handleSubmit = (values: typeof form.values) => {
    registerUser(values).then((data)=>{
      console.log("User registered successfully:", data);
      successNotification("User registered successfully!");
      console.log("Redirecting to login page");
      navigate("/login");
 
    }).catch((error)=>{
      errorNotification(error.response.data.errorMessage);
      console.error("Registration failed:", error);
    }).finally(() => setLoading(false));
};

  return (
    <div
      style={{ background: 'url("/hs.jpg")' }}
      className="h-screen !bg-no-repeat !bg-cover !bg-center w-screen flex flex-col items-center justify-center  "
    >
      <div className=" py-3 flex  text-pink-500  gap-1 ">
        <IconHeartbeat size={45} stroke={2.5} />
        <span className="font-handling font-semibold text-4xl">Pulse</span>
      </div>

      <div className="w-[450px] backdrop-blur-md p-10 py-8 rounded-lg ">
        <form onSubmit={form.onSubmit(handleSubmit)} className="flex flex-col gap-5 [&_input]:placeholder-black [&_.mantine-Input-input]:!border-white focus-within:[&_.mantine-Input-input]:!border-pink-400 [&_.mantine-Input-input]:!border [&_input]:pl-2 [&_svg]:text-black [&_input}:!text-white">
          <div className="self-center font-medium bg-fontFamily-heading text-white text-xl">
            Register{" "}
          </div>
           <SegmentedControl bg="none" className="[&_*]:!text-white border border-white" color="pink" {...form.getInputProps("type")} fullWidth size="md" radius="md" data={[{label:'Patient',value:"PATIENT"}, {label:'Doctor',value:"DOCTOR"},{label:'Admin',value:"ADMIN"},]} />
          <TextInput
          className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="name"
              {...form.getInputProps('name')}
          />
          <TextInput
          className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Email"
              {...form.getInputProps('email')}
          />
          <PasswordInput
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Password"
            className="transition duration-300"
              {...form.getInputProps('password')}
          />
          <PasswordInput
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Confirm Password"
            className="transition duration-300"
              {...form.getInputProps('confirmPassword')}
          />
          <Button loading={loading} radius="md" size="md" type="submit" color="pink">
            register
          </Button>
          <div className="text-sm self-center">
            Already have an account?
            <Link to="/login" className="hover:underline hover:text-blue-700"  > Sign In</Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
