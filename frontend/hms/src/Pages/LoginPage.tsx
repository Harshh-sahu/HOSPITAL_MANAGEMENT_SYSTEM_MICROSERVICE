import { Button, PasswordInput, TextInput } from "@mantine/core";
import { IconHeartbeat } from "@tabler/icons-react";
import React from "react";
import { useForm } from '@mantine/form';
import { Link } from "react-router-dom";


const LoginPage = () => {

 const form = useForm({

    initialValues: {
      email: '',
      password: '',
    },

    validate: {
  email: (value: string) =>
    /^\S+@\S+$/.test(value) ? null : 'Invalid email',

  password: (value: string) =>
    value.length >= 6 ? null : 'Password must be at least 6 characters long',
},
  });

   const handleSubmit = (values: typeof form.values) => {
    console.log(values);
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
            Login{" "}
          </div>
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
          <Button radius="md" size="md" type="submit" color="pink">
            Login
          </Button>
          <div className="text-sm self-center">
            Don't have an account?
            <Link to="/register" className="hover:underline hover:text-blue-700" >Register</Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
