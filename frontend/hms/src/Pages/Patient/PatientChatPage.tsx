import { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import {
  ActionIcon,
  Box,
  Card,
  Center,
  Group,
  Loader,
  ScrollArea,
  Stack,
  Text,
  TextInput,
  Tooltip,
} from "@mantine/core";
import { IconRefresh, IconRobot, IconSend } from "@tabler/icons-react";
import { clearChatHistory, sendChatMessage } from "../../Service/ChatService";

interface Message {
  role: "user" | "bot";
  text: string;
}

const CONV_KEY = "hms_chat_id";

function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
}

function getOrCreateConversationId(): string {
  let id = localStorage.getItem(CONV_KEY);
  if (!id) {
    id = generateId();
    localStorage.setItem(CONV_KEY, id);
  }
  return id;
}

const PatientChatPage = () => {
  const user = useSelector((state: any) => state.user);
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId, setConversationId] = useState<string>(() =>
    getOrCreateConversationId()
  );
  const viewport = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (viewport.current) {
      viewport.current.scrollTo({ top: viewport.current.scrollHeight, behavior: "smooth" });
    }
  }, [messages, isLoading]);

  const handleSend = async () => {
    const text = input.trim();
    if (!text || isLoading) return;
    setInput("");
    setMessages((prev) => [...prev, { role: "user", text }]);
    setIsLoading(true);
    try {
      const data = await sendChatMessage({
        message: text,
        conversationId,
        role: user.role,
        userName: user.name,
        profileId: user.profileId,
      });
      setMessages((prev) => [...prev, { role: "bot", text: data.response }]);
    } catch {
      setMessages((prev) => [
        ...prev,
        { role: "bot", text: "Sorry, I couldn't reach the server. Please try again." },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNewChat = async () => {
    await clearChatHistory(conversationId).catch(() => {});
    const newId = generateId();
    localStorage.setItem(CONV_KEY, newId);
    setConversationId(newId);
    setMessages([]);
  };

  return (
    <div className="p-5 flex flex-col h-[calc(100vh-80px)]">
      <Group justify="space-between" mb="md">
        <Group gap={8}>
          <IconRobot size={26} className="text-primary-500" />
          <h2 className="text-2xl font-semibold text-primary-600">AI Assistant</h2>
        </Group>
        <Tooltip label="New chat" withArrow>
          <ActionIcon variant="light" color="primary" onClick={handleNewChat}>
            <IconRefresh size={18} />
          </ActionIcon>
        </Tooltip>
      </Group>

      <Card shadow="sm" radius="lg" withBorder className="flex flex-col flex-1 overflow-hidden">
        <ScrollArea flex={1} viewportRef={viewport} p="sm">
          <Stack gap={10} p={4}>
            {messages.length === 0 && (
              <Center h={200}>
                <Stack align="center" gap={8}>
                  <IconRobot size={48} stroke={1.2} className="text-gray-300" />
                  <Text c="dimmed" size="sm">
                    Ask me anything about appointments, doctors, medicines, and more!
                  </Text>
                </Stack>
              </Center>
            )}
            {messages.map((msg, i) => (
              <Box
                key={i}
                style={{
                  display: "flex",
                  justifyContent: msg.role === "user" ? "flex-end" : "flex-start",
                }}
              >
                <Box
                  style={{
                    maxWidth: "70%",
                    padding: "10px 14px",
                    borderRadius:
                      msg.role === "user"
                        ? "16px 16px 4px 16px"
                        : "16px 16px 16px 4px",
                    background:
                      msg.role === "user"
                        ? "var(--mantine-color-primary-4)"
                        : "var(--mantine-color-gray-1)",
                    color:
                      msg.role === "user"
                        ? "white"
                        : "var(--mantine-color-dark-7)",
                  }}
                >
                  <Text size="sm" style={{ whiteSpace: "pre-wrap", wordBreak: "break-word" }}>
                    {msg.text}
                  </Text>
                </Box>
              </Box>
            ))}
            {isLoading && (
              <Box style={{ display: "flex", justifyContent: "flex-start" }}>
                <Box
                  style={{
                    padding: "10px 14px",
                    borderRadius: "16px 16px 16px 4px",
                    background: "var(--mantine-color-gray-1)",
                  }}
                >
                  <Loader size="xs" color="primary" type="dots" />
                </Box>
              </Box>
            )}
          </Stack>
        </ScrollArea>

        <Box p="sm" style={{ borderTop: "1px solid var(--mantine-color-gray-2)" }}>
          <Group gap={8}>
            <TextInput
              flex={1}
              placeholder="Type your message…"
              value={input}
              onChange={(e) => setInput(e.currentTarget.value)}
              onKeyDown={(e) => e.key === "Enter" && !e.shiftKey && handleSend()}
              disabled={isLoading}
              radius="xl"
            />
            <ActionIcon
              size={40}
              radius="xl"
              variant="filled"
              color="primary"
              onClick={handleSend}
              disabled={isLoading || !input.trim()}
            >
              <IconSend size={18} />
            </ActionIcon>
          </Group>
        </Box>
      </Card>
    </div>
  );
};

export default PatientChatPage;
